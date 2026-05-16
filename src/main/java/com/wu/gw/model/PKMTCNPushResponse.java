package com.wu.gw.model;

public class PKMTCNPushResponse {
    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "PKMTCNPushResponse{" +
                "response=" + response +
                '}';
    }

    public class Response {
        private String responseCode;
        private String message;

        public String getResponseCode() {
            return responseCode;
        }

        public void setResponseCode(String responseCode) {
            this.responseCode = responseCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "Response{" +
                    "responseCode='" + responseCode + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}
