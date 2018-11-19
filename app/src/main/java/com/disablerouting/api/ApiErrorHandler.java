package com.disablerouting.api;

import com.disablerouting.utils.Utility;

import java.util.HashMap;
import java.util.Map;

public class ApiErrorHandler {

    private static final Map<String, String> ERROR_MAP = createErrorMap();


    private static Map<String, String> createErrorMap() {
        Map<String, String> errorConstantMap = new HashMap<String, String>();
        String mLangApplication  = Utility.getAppLanguage();
        if(mLangApplication.equalsIgnoreCase("English")) {
            errorConstantMap.put("2000", "Your request could not be processed. Please send us a mail via contact in case the problem occurs again.");
            errorConstantMap.put("2001", "Please check your input. A value is missing.");
            errorConstantMap.put("2002", "Please check your input. A value is wrong");
            errorConstantMap.put("2003", "Please check your input. A value is wrong.");
            errorConstantMap.put("2004", "Please check your inupt. Start or destination are out of the boundaries for the routing.");
            errorConstantMap.put("2006", "Your request could not be processed. Please send us a mail via contact in case the problem occurs again.");
            errorConstantMap.put("2007", "Your request could not be processed. Please send us a mail via contact in case the problem occurs again.");
            errorConstantMap.put("2008", "Please check your input. A value is missing.");
            errorConstantMap.put("2009", "Route can not be found.");
            errorConstantMap.put("2099", "Your request could not be processed. Please send us a mail via contact in case the problem occurs again.");
        }else {
            errorConstantMap.put("2000", "Deine Anfrage konnte leider nicht bearbeitet werden. Bitte sende uns eine Mail über das Kontaktformular wenn das Problem noch einmal auftritt");
            errorConstantMap.put("2001", "Bitte überprüfe deine Eingabe. Es fehlt ein Wert.");
            errorConstantMap.put("2002", "Bitte überprüfe deine Eingabe. Ein Wert wurde falsch eingegeben.");
            errorConstantMap.put("2003", "Bitte überprüfe deine Eingabe. Ein Wert wurde falsch eingegeben");
            errorConstantMap.put("2004", "Bitte überprüfe deine Eingabe. Start oder Ziel befinden sich außerhalb des Routinggebiets.");
            errorConstantMap.put("2006", "Deine Anfrage konnte leider nicht bearbeitet werden. Bitte sende uns eine Mail über das Kontaktformular wenn das Problem noch einmal auftritt.");
            errorConstantMap.put("2007", "Deine Anfrage konnte leider nicht bearbeitet werden. Bitte sende uns eine Mail über das Kontaktformular wenn das Problem noch einmal auftritt.");
            errorConstantMap.put("2008", "Bitte überprüfe deine Eingabe. Es fehlt ein Wert.");
            errorConstantMap.put("2009", "Route kann nicht gefunden werden.");
            errorConstantMap.put("2099", "Deine Anfrage konnte leider nicht bearbeitet werden. Bitte sende uns eine Mail über das Kontaktformular wenn das Problem noch einmal auftritt.");
        }
        return errorConstantMap;
    }

    /**
     * Checks whether the look up table contains the message entry for the given error code.
     * @param errorCode the error code.
     * @return true if the mapping exists false otherwise.
     */
    private static boolean isResolvable(String errorCode) {
        return ERROR_MAP.containsKey(errorCode);
    }


    /**
     * Resolves the error code to the display message.
     * @param errorCode the error code.
     * @return the translated message equivalent of the error code.
     */
    public static String resolve(String errorCode) {
        if(Utility.getAppLanguage().equalsIgnoreCase("English")) {
            return isResolvable(errorCode) ? ERROR_MAP.get(errorCode) : "Your request could not be processed. Please send us a mail via contact in case the problem occurs again.";
        }else {
            return isResolvable(errorCode) ? ERROR_MAP.get(errorCode) : "Deine Anfrage konnte leider nicht bearbeitet werden. Bitte sende uns eine Mail über das Kontaktformular wenn das Problem noch einmal auftritt.";
        }
    }
}
