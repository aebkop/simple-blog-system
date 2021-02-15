package cc.benhull.simpleblog.postservice;

import io.vertx.core.AbstractVerticle;
import io.vertx.serviceproxy.ServiceBinder;

public class PostsVerticle extends AbstractVerticle {
    @Override
    public void start() {
        new ServiceBinder(vertx)
            .setAddress("posts-service")
            .register(PostsService.class, PostsService.create(vertx));
    }
}
