package ch.jbert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Source {
    private static final Logger logger = LoggerFactory.getLogger(Source.class);

    public void test() {
        logger.error("HELLO WORLD");
    }
}

