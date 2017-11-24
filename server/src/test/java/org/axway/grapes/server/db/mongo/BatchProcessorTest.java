package org.axway.grapes.server.db.mongo;

import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.util.InjectionUtils;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class BatchProcessorTest {

    @Test
    public void consumerIsUsedOnAllReturnedEntities() {
        List<String> fullList = generateList(4);
        List<String> batch = generateList(1);

        final BatchProcessor sut = makeSut(batch, 1);

        Consumer<String> consumerMock = mock(Consumer.class);

        sut.process("Some collection",
                b -> "",
                fullList,
                String.class,
                consumerMock);

        verify(consumerMock, times(fullList.size())).accept(anyString());
    }

    @Test
    public void missingEntriesGenerateLogMessages() throws NoSuchFieldException, IllegalAccessException {
        List<String> fullList = generateList(4);
        List<String> batch = generateList(3);

        final BatchProcessor sut = makeSut(batch, fullList.size());
        final Logger fakeLogger = mock(Logger.class);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        InjectionUtils.injectField(sut, BatchProcessor.class, "LOG", fakeLogger);

        // Process
        sut.process("Some collection",
                b -> "",
                fullList,
                String.class,
                mock(Consumer.class));

        // Assert
        final List<String> allValues = captor.getAllValues();

        verify(fakeLogger, times(2)).warn(captor.capture());
        assertEquals("Got fewer results 3 < 4", allValues.get(0));
        assertEquals("There are referred dependencies not related to known artifacts", allValues.get(1));
    }


    @Test
    public void splitListWorksCorrectExact() throws NoSuchFieldException, IllegalAccessException {
        final List<String> fullList = generateList(6);
        final List<String> batch = generateList(3);

        final BatchProcessor sut1 = makeSut(batch, batch.size());
        final Function<List<String>, String> fnMock = mock(Function.class);

        // Process
        sut1.process("Some collection",
                fnMock,
                fullList,
                String.class,
                mock(Consumer.class));

        // Assert
        verify(fnMock, times(getExpectedBatchCount(fullList, batch))).apply(anyListOf(String.class));
    }

    @Test
    public void splitListWorksCorrectReminder() throws NoSuchFieldException, IllegalAccessException {
        final List<String> fullList = generateList(5);
        final List<String> batch = generateList(2);
        final BatchProcessor sut1 = makeSut(batch, batch.size());

        final Function<List<String>, String> fnMock = mock(Function.class);

        // Process
        sut1.process("Some collection",
                fnMock,
                fullList,
                String.class,
                mock(Consumer.class));

        // Assert
        verify(fnMock, times(getExpectedBatchCount(fullList, batch))).apply(anyListOf(String.class));
    }

    private int getExpectedBatchCount(List<String> fullList, List<String> batch) {
        return fullList.size() % batch.size() == 0 ?
                    fullList.size() / batch.size() :
                    fullList.size() / batch.size() + 1;
    }

    private List<String> generateList(final int howMany) {
        List<String> result = new ArrayList<>();
        for(int i = 1; i <= howMany; i++) {
            result.add(String.format("entry_%s", i));
        }

        return result;
    }

    private BatchProcessor makeSut(List<String> batch, int batchSize) {
        final RepositoryHandler repoHandler = mock(RepositoryHandler.class);
        when(repoHandler.getListByQuery(anyString(), anyString(), eq(String.class)))
                .thenReturn(batch);

        final BatchProcessor result = new BatchProcessor(repoHandler);
        result.setBatchSize(batchSize);

        return result;
    }
}
