package cc.benhull.simpleblog.publicapi;

import cc.benhull.simpleblog.postservice.PostsService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.impl.LoggerHandlerImpl;

import static io.vertx.ext.web.handler.LoggerFormat.TINY;

public class APIVerticle extends AbstractVerticle {
    private PostsService postsService;
    private WebClient webClient;
    private final Logger logger = LoggerFactory.getLogger(APIVerticle.class);


    public void start() {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        String prefix = "/api/v1";
        WebClient webClient = WebClient.create(vertx);
        this.postsService = PostsService
            .createProxy(vertx, "posts-service");
        this.webClient = webClient;

        //posts
        router.get(prefix + "/:postid").handler(this::getPost);
        //TODO handle pagination
        router.get(prefix + "/posts").handler(this::getPosts);
        //comments
        //TODO handle threaded comments
        router.get(prefix + "/:postid/comments").handler(this::comments);
        //other?
        router.get("test").respond(ctx -> ctx.end("test"));

        router.route().handler(ctx -> {

            // This handler will be called for every request
            HttpServerResponse response = ctx.response();
            response.putHeader("content-type", "text/plain");

            // Write to the response and end it
            response.end("Hello World from Vert.x-Web!");
        });

        logger.info("API service started");


        server.requestHandler(router).listen(8080);


    }

    private void getPost(RoutingContext routingContext) {
        String postID = routingContext.pathParam("postid");
        postsService.getPost(postID,
            resp -> {
                if (resp.succeeded()) {
                    routingContext.response().putHeader("content-type", "application/json").end(resp.result().encode());
                } else {
                    routingContext.response().end("lol");
                }
            });
    }

    private void getPosts(RoutingContext routingContext) {
        postsService.getPosts(
            resp -> {
                if (resp.succeeded()) {
                    routingContext.response().putHeader("content-type", "application/json").end(resp.result().encode());
                } else {
                    routingContext.response().setStatusCode(503).end();
                }
            });
    }

    private void comments(RoutingContext routingContext) {
        webClient
            .get(3001, "localhost", "/posts")
            .expect(ResponsePredicate.SC_SUCCESS)
            .as(BodyCodec.jsonObject())
            .send()
            .map(HttpResponse::body);
    }


}
