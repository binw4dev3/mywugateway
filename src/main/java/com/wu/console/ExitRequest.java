package com.wu.console;

import com.wu.WUGWRuntime;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ExitRequest extends AbstractServiceRequest {

        @Value("${ExitRequest.serialNum}")
        private String serialNum;

        @Value("${ExitRequest.description}")
        private String descriptionValue;

        public ExitRequest() {
                super();
        }

        @PostConstruct
        private void init() {
                setRequestID(serialNum);
                setDescription(descriptionValue);
        }
        
        @Override
        protected boolean doService() {
                return false;
        }
}
