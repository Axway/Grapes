package org.axway.grapes.server.reports.writer;

import org.axway.grapes.server.reports.impl.Report;
import org.axway.grapes.server.reports.impl.ReportExecution;
import org.axway.grapes.server.reports.impl.ReportsRegistry;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Provider
@Produces("text/csv")
public class CsvReportWriter implements MessageBodyWriter<ReportExecution> {
    private static final String LINE_SEP = System.getProperty("line.separator");
    private static final String COMMA = ",";

    private String report = "N/A";

    // The serialization framework invokes the methods in the following sequence:
    // getSize and writeTo. To provide a meaningful response to getSize, the actual
    // serialized form of the report execution needs to be produced. The writeTo
    // should only retrieve this already computed form and write it to the output
    // stream. For avoiding the reserialization, csvReports collection is used.
    //
    private Map<ReportExecution, String> csvReports = new HashMap<>();

    @Override
    public boolean isWriteable(Class<?> aClass,
                               Type type,
                               Annotation[] annotations,
                               MediaType mediaType) {
        return type == ReportExecution.class;
    }

    @Override
    public long getSize(ReportExecution reportExecution,
                        Class<?> aClass,
                        Type type,
                        Annotation[] annotations,
                        MediaType mediaType) {

        try {
            report = this.computeReport(reportExecution);
            csvReports.put(reportExecution, report);
            return report.length();
        } catch (IOException e) {
            report = "N/A";
            return 0;
        }
    }

    @Override
    public void writeTo(ReportExecution reportExecution,
                        Class<?> aClass,
                        Type type,
                        Annotation[] annotations,
                        MediaType mediaType,
                        MultivaluedMap<String, Object> multivaluedMap,
                        OutputStream outputStream)
            throws IOException {

        if (csvReports.containsKey(reportExecution)) {
            outputStream.write(csvReports.get(reportExecution).getBytes("UTF-8"));
        } else {
            outputStream.write("Report execution not available".getBytes("UTF-8"));
        }
    }

    private String computeReport(final ReportExecution reportExecution) throws IOException {
        StringBuilder buffer = new StringBuilder(getHeader(reportExecution));
        int i;
        final List<String[]> dataList = reportExecution.getData();
        for (String[] row : dataList) {
            for (i = 0; i < row.length; i++) {
                buffer.append(row[i].replaceAll(",", " "));

                if (i < row.length - 1) {
                    buffer.append(COMMA);
                }
            }

            buffer.append(LINE_SEP);
        }
        ;

        return buffer.toString();
    }

    private String getHeader(ReportExecution e) {
        final StringBuilder b = new StringBuilder("Report Execution");
        final Optional<Report> byId = ReportsRegistry.findById(e.getRequest().getReportId());
        if (byId.isPresent()) {
            b.append(LINE_SEP);
            b.append(byId.get().getName());
            b.append(LINE_SEP);
            b.append(byId.get().getDescription());
        }
        b.append(LINE_SEP);
        b.append("Parameters: ");
        b.append(LINE_SEP);
        final Map<String, String> paramValues = e.getRequest().getParamValues();

        for (String key : paramValues.keySet()) {
            b.append(key);
            b.append(COMMA);
            b.append(paramValues.get(key));
            b.append(LINE_SEP);
        }

        b.append(LINE_SEP);
        b.append(LINE_SEP);

        for (String col : e.getResultColumnNames()) {
            b.append(col);
            b.append(COMMA);
        }
        b.setCharAt(b.length() - 1, ' ');
        b.append(LINE_SEP);


        return b.toString();
    }
}
