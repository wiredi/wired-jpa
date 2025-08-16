package com.wiredi.jpa.eclipse;

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomSLF4JLogger extends AbstractSessionLog {
    
    @Override
    public void log(SessionLogEntry entry) {
        if (!shouldLog(entry.getLevel(), entry.getNameSpace())) {
            return;
        }
        
        Logger logger = LoggerFactory.getLogger("org.eclipse.persistence." + entry.getNameSpace());
        String message = formatMessage(entry);
        
        switch (entry.getLevel()) {
            case SessionLog.SEVERE:
                logger.error(message);
                break;
            case SessionLog.WARNING:
                logger.warn(message);
                break;
            case SessionLog.INFO:
                logger.info(message);
                break;
            case SessionLog.CONFIG:
                logger.debug(message);
                break;
            case SessionLog.FINE:
            case SessionLog.FINER:
            case SessionLog.FINEST:
                logger.trace(message);
                break;
        }
    }
    
    @Override
    public boolean shouldLog(int level, String category) {
        return level >= getLevel(category);
    }
}