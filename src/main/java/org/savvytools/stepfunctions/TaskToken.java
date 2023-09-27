package org.savvytools.stepfunctions;

import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.model.SendTaskFailureRequest;
import software.amazon.awssdk.services.sfn.model.SendTaskSuccessRequest;

import java.util.HashMap;
import java.util.Map;

public class TaskToken {

    private static String generateJSON(String status, Map<String, String> params) {
        Map<String, String> data = new HashMap<>();
        data.put("Status", status);
        if (params != null) data.putAll(params);
        try {
            return new ObjectMapper().writeValueAsString(data);
        } catch (Exception e) {
            return "{}";
        }
    }

    /**
     * Will send task success notificaiton to the stepfunction.
     * @param token - the token passed to the service by step function.
     *
     *  {
     *      "Status" : "Success"
     *
     *  }
     */
    public static void success(String token) {
        success(token, new HashMap<String, String>());
    }
    public static void success(String token, Map<String, String> params) {
        SendTaskSuccessRequest req = SendTaskSuccessRequest
                .builder()
                .taskToken(token)
                .output(generateJSON("Success", params))
                .build();

        try {
           SfnClient.builder()
                   .region(Region.US_EAST_1)
                    .build()
                    .sendTaskSuccess(req);;

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Will send task failure notificaiton to the stepfunction.
     * @param token - the token passed to the service by step function.
     */
    public static void fail(String token) {
        SendTaskFailureRequest req = SendTaskFailureRequest
                .builder()
                .taskToken(token)
                .error("Task execution failed")
                .cause("Task execution unknow error")
                .build();

        try {
            SfnClient.builder()
                    .region(Region.US_EAST_1)
                    .build()
                    .sendTaskFailure(req);;

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Return true if the first argument of the given array is a callback token from step function
     * @param args
     * @return true if there is a callback token present
     */
    public static boolean containsCallbackToken(String args[]) {
        if (args == null) return false;
        if (args.length < 1) return false;
        if (args[0] == null) return false;
        if (args[0].length() < 100) return false;
        if (args[0].indexOf(' ') >=0 ) return false;

        return true;
    }
}
