package cc.benhull.simpleblog;

import cc.benhull.simpleblog.postservice.PostsVerticle;
import cc.benhull.simpleblog.publicapi.APIVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class MainVerticle extends AbstractVerticle {
    private final Logger logger = LoggerFactory.getLogger(MainVerticle.class);
    @Override
    public void start() {
        APIVerticle apiVerticle = new APIVerticle();
        vertx.deployVerticle(apiVerticle);
        PostsVerticle postsVerticle = new PostsVerticle();
        vertx.deployVerticle(postsVerticle);
    }

    @Override
    public void stop() {
        logger.info("Stop");
    }


    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MainVerticle());

    }


}
