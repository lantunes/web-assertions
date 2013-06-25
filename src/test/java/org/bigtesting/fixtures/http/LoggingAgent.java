package org.bigtesting.fixtures.http;

import java.nio.channels.SocketChannel;

import org.simpleframework.http.core.ContainerEvent;
import org.simpleframework.transport.trace.Agent;
import org.simpleframework.transport.trace.Trace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingAgent implements Agent {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAgent.class);
    
    public Trace attach(SocketChannel channel) {
        return new LoggingTrace();
    }

    public void stop() {
    }

    private class LoggingTrace implements Trace {

        public void trace(Object event) {
        }

        public void trace(Object event, Object value) {

            if (ContainerEvent.ERROR.equals(event)) {
                logger.error("server encountered error", value);
            }
        }
        
    }
}
