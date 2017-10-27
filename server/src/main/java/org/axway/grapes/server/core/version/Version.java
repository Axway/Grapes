package org.axway.grapes.server.core.version;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Version Model Class
 * 
 * @author jdcoffre
 */
public class Version {

    private static final Logger LOG = LoggerFactory.getLogger(Version.class);

    private final String stringVersion;
	private static final String SNAPSHOT = "SNAPSHOT";
    private static final String SPLITTER_PATTERN = "(\\.|-)";


    public static Optional<Version> make(final String v) {
	    if(!isValid(v)) {
            return Optional.empty();
        }

        return Optional.of(new Version(v));
    }

    private Version(final String version) {
        this.stringVersion = version;
    }

	/**
	 * Check if a version is a snapshot
	 * 
	 * @return true if the version is a snapshot
	 */
	public Boolean isSnapshot() {
		return stringVersion.contains("SNAPSHOT");
	}

	/**
	 * Check if a version is a release version
	 * 
	 * @return true if the version is a release  
	 */
	public Boolean isRelease() {
		return !isSnapshot();
	}

	/**
	 * Check if a version is a branch version
	 * 
	 * @return true if the version is a branch version
	 */
	public Boolean isBranch() {
		return stringVersion.split("-").length == 3;
	}

	/**
	 * Return the digits of a version
	 * 
	 * @return digits
	 */
	private String getDigits() {
		return stringVersion.split("-")[0];
	}

	/**
	 * Return the number of digit of the version
	 * 
	 * @return number of digit
	 */
	private Integer getDigitsSize() {
		return getDigits().split("\\.").length;
	}

	/**
	 * Return the ieme digit of the version 
	 * 
	 * @param i digit number
	 * @return digit
	 */
	private Integer getDigit(final int i) {
		return Integer.valueOf(getDigits().split("\\.")[i]);
	}

	/**
	 * Return the releaseId
	 * 
	 * @return releaseId
	 */
	private Integer getReleaseId() {
		final String[] versionParts = stringVersion.split("-");
		
		if(isBranch() && versionParts.length >= 3){
			return Integer.valueOf(versionParts[2]);
		}
		else if(versionParts.length >= 2){
			return Integer.valueOf(versionParts[1]);
		}

		return 0;
	}

	/**
	 * Return the branchId
	 * 
	 * @return branchId
	 */
	private String getBranchId() {
		return stringVersion.split("-")[1];
	}

	/**
	 * Compare two versions
	 * 
	 * @param other
	 * @return an integer: 0 if equals, -1 if older, 1 if newer
	 * @throws IncomparableException is thrown when two versions are not coparable
	 */
	public int compare(final Version other) throws IncomparableException{
		// Cannot compare branch versions and others 
		if(!isBranch().equals(other.isBranch())){
			throw new IncomparableException();
		}
		
		// Compare digits
		final int minDigitSize = getDigitsSize() < other.getDigitsSize()? getDigitsSize(): other.getDigitsSize();
		
		for(int i = 0; i < minDigitSize ; i++){
			if(!getDigit(i).equals(other.getDigit(i))){
				return getDigit(i).compareTo(other.getDigit(i));
			}
		}
		
		// If not the same number of digits and the first digits are equals, the longest is the newer
		if(!getDigitsSize().equals(other.getDigitsSize())){
			return getDigitsSize() > other.getDigitsSize()? 1: -1;
		}

        if(isBranch() && !getBranchId().equals(other.getBranchId())){
			return getBranchId().compareTo(other.getBranchId());
		}
		
		// if the digits are the same, a snapshot is newer than a release
		if(isSnapshot() && other.isRelease()){
			return 1;
		}
		
		if(isRelease() && other.isSnapshot()){
			return -1;
		}
		
		// if both versions are releases, compare the releaseID
		if(isRelease() && other.isRelease()){
			return getReleaseId().compareTo(other.getReleaseId());
		}
		
		return 0;
	}

	@Override
	public String toString(){
		return stringVersion;
	}


	public static boolean isValid(final String str) {
        if(null == str) {
            return false;
        }

        final String[] versionsParts = str.split("-");
        if(versionsParts.length > 3) {
            return false;
        }

        final String[] parts = str.split(SPLITTER_PATTERN);

        final Set<String> badParts = Arrays.stream(parts)
                .filter(part -> !part.equals(SNAPSHOT))
                .filter(part -> {
                    try {
                        int v = Integer.parseInt(part);
                        return false;
                    } catch (NumberFormatException e) {
                        return true;
                    }
                })
                .collect(Collectors.toSet());

        if(!badParts.isEmpty() && LOG.isDebugEnabled()) {
            badParts.forEach(badPart -> LOG.debug(String.format("Invalid version part identified \"%s\" in %s", badPart, str)));
        }

        return badParts.isEmpty();
    }
}
