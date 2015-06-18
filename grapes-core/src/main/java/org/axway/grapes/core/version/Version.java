package org.axway.grapes.core.version;

/**
 * Version Model Class
 *
 * @author jdcoffre
 */
public class Version {

    private final String stringVersion;

    public Version(final String version) throws NotHandledVersionException {
        this.stringVersion = version;
        // Checks if the version match the expectations
        final String[] versionsParts = stringVersion.split("-");
        if (versionsParts.length > 3) {
            throw new NotHandledVersionException();
        }
        try {
            for (String digit : getDigits().split("\\.")) {
                Integer.parseInt(digit);
            }
            if (versionsParts.length > 1 && !versionsParts[1].contains("SNAPSHOT")) {
                Integer.parseInt(versionsParts[1]);
            }
            if (versionsParts.length > 2 && !versionsParts[2].contains("SNAPSHOT")) {
                Integer.parseInt(versionsParts[2]);
            }
        } catch (NumberFormatException e) {
            throw new NotHandledVersionException(e);
        }
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
        if (isBranch() && versionParts.length >= 3) {
            return Integer.valueOf(versionParts[2]);
        } else if (versionParts.length >= 2) {
            return Integer.valueOf(versionParts[1]);
        }
        return 0;
    }

    /**
     * Return the branchId
     *
     * @return branchId
     */
    private Integer getBranchId() {
        return Integer.valueOf(stringVersion.split("-")[1]);
    }

    /**
     * Compare two versions
     *
     * @param other
     * @return an integer: 0 if equals, -1 if older, 1 if newer
     * @throws IncomparableException is thrown when two versions are not coparable
     */
    public int compare(final Version other) throws IncomparableException {
        // Cannot compare branch versions and others
        if (!isBranch().equals(other.isBranch())) {
            throw new IncomparableException();
        }
        // Compare digits
        final int minDigitSize = getDigitsSize() < other.getDigitsSize() ? getDigitsSize() : other.getDigitsSize();
        for (int i = 0; i < minDigitSize; i++) {
            if (!getDigit(i).equals(other.getDigit(i))) {
                return getDigit(i).compareTo(other.getDigit(i));
            }
        }
        // If not the same number of digits and the first digits are equals, the longest is the newer
        if (!getDigitsSize().equals(other.getDigitsSize())) {
            return getDigitsSize() > other.getDigitsSize() ? 1 : -1;
        }
        if (isBranch() && !getBranchId().equals(other.getBranchId())) {
            return getBranchId().compareTo(other.getBranchId());
        }
        // if the digits are the same, a snapshot is newer than a release
        if (isSnapshot() && other.isRelease()) {
            return 1;
        }
        if (isRelease() && other.isSnapshot()) {
            return -1;
        }
        // if both versions are releases, compare the releaseID
        if (isRelease() && other.isRelease()) {
            return getReleaseId().compareTo(other.getReleaseId());
        }
        return 0;
    }

    @Override
    public String toString() {
        return stringVersion;
    }
}
