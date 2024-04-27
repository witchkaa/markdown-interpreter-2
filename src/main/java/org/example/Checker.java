package org.example;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Checker {
    public boolean checkForUnbalancedMarkers(String marker, String blueprint) {
        int firstOcc = -1;
        int secondOcc = -1;

        for (int i = 0; i < blueprint.length(); i++) {
            if (isMarkerAtIndex(marker, blueprint, i)) {
                boolean atWordEnd = isAtWordEnd(blueprint, i, marker.length());
                boolean beforeIsMatch = isBeforeMatch(blueprint, i, marker);
                boolean afterIsMatch = isAfterMatch(blueprint, i, marker);
                if (isInvalidMarkerPosition(atWordEnd, beforeIsMatch, afterIsMatch)) {
                    continue;
                }
                if (isOpenPosition(firstOcc, secondOcc)) {
                    firstOcc = i;
                } else {
                    secondOcc = i;
                }
            }
            if (isOpenNotClosed(firstOcc, secondOcc, blueprint, i)) {
                return true;
            }
        }
        return isOpenNotClosed(firstOcc, secondOcc, blueprint, blueprint.length() - 1);
    }

    private boolean isMarkerAtIndex(String marker, String blueprint, int idx) {
        return blueprint.startsWith(marker, idx);
    }

    private boolean isAtWordEnd(String blueprint, int idx, int markerLength) {
        return (idx > 0 && Character.isLetterOrDigit(blueprint.charAt(idx - 1))) &&
                (idx + markerLength == blueprint.length() || !Character.isLetterOrDigit(blueprint.charAt(idx + markerLength)));
    }

    private boolean isBeforeMatch(String blueprint, int idx) {
        String regex = "[A-Za-z0-9,\\u0400-\\u04FF]";
        return idx > 0 && Character.toString(blueprint.charAt(idx - 1)).matches(regex);
    }

    private boolean isAfterMatch(String blueprint, int idx, String marker) {
        String regex = "[A-Za-z0-9,\\u0400-\\u04FF]";
        return idx + marker.length() < blueprint.length() && Character.toString(blueprint.charAt(idx + marker.length())).matches(regex);
    }

    private boolean isInvalidMarkerPosition(boolean atWordEnd, boolean beforeIsMatch, boolean afterIsMatch) {
        return atWordEnd || ((!beforeIsMatch && !afterIsMatch) || (beforeIsMatch && afterIsMatch));
    }

    private boolean isOpenPosition(int openPos, int closePos) {
        return openPos == -1 || closePos != -1;
    }

    private boolean isOpenNotClosed(int openPos, int closePos, String blueprint, int idx) {
        return openPos != -1 && closePos == -1 && (blueprint.charAt(idx) == '\n' || idx == blueprint.length() - 1);
    }
    public boolean checkForNestedMarkers(String firstMarker, String secondMarker, List<String> blocks){
        Pattern firstMarkerPattern = Pattern.compile(firstMarker, Pattern.DOTALL);
        Pattern secondMarkerPattern = Pattern.compile(secondMarker, Pattern.DOTALL);
        boolean isFirstMatch = false;
        boolean isSecondMatch = false;
        for (String b : blocks) {
            Matcher firstMarkerMatcher = firstMarkerPattern.matcher(b);
            Matcher secondMarkerMatcher = secondMarkerPattern.matcher(b);
            isFirstMatch = firstMarkerMatcher.find();
            isSecondMatch = secondMarkerMatcher.find();
            if (isFirstMatch || isSecondMatch) break;
        }
        return isFirstMatch || isSecondMatch;
    }
}
