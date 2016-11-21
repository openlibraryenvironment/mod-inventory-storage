package org.folio.rest.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import org.folio.rest.jaxrs.model.Item;
import org.folio.rest.jaxrs.model.Items;
import org.folio.rest.jaxrs.resource.ItemStorageResource;
import org.folio.rest.tools.utils.OutStream;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.Response;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

import org.folio.rest.persist.PostgresClient;

public class ItemStorageAPI implements ItemStorageResource {

    public Map<String,Item> storage = new HashMap<String,Item>();
    
  @Override
  public void getItemStorageItem(@DefaultValue("en") @Pattern(regexp = "[a-zA-Z]{2}") String lang, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    List<Item> items = new ArrayList<Item>();
    Items itemList = new Items();
    itemList.setItems(items);
    itemList.setTotalRecords(items.size());
    asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(
      ItemStorageResource.GetItemStorageItemResponse.withJsonOK(itemList)));
  }

  @Override
  public void postItemStorageItem(@DefaultValue("en") @Pattern(regexp = "[a-zA-Z]{2}") String lang, Item entity, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
      
      OutStream stream = new OutStream();
      UUID id = UUID.randomUUID();
      String idString = id.toString();
      entity.setId(idString);
      storage.put(idString,entity);
      stream.setData(storage.get(idString));
      asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(
								     ItemStorageResource.PostItemStorageItemResponse
								     .withJsonCreated("I dont know please dont shoot me",stream)));
      /*
  try {
      System.out.println("sending... Item");
      PostgresClient postgresClient = PostgresClient.getInstance(vertxContext.owner());

      vertxContext.runOnContext(v -> {

        try {
          postgresClient.save("Table-name", entity,
                  reply -> {
                    try {
                      Item p = entity;
                      p.setId(reply.result());
                      OutStream stream = new OutStream();
                      stream.setData(p);
		      asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(
								     ItemStorageResource.PostItemStorageItemResponse
								     .withJsonCreated(reply.result(),stream)));
                      } catch (Exception e) {
                      e.printStackTrace();
		      asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(
								     ItemStorageResource.PostItemStorageItemResponse
								     .withPlainInternalServerError());
                    }
                  });
        } catch (Exception e) {
          e.printStackTrace();
	  asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(
								     ItemStorageResource.PostItemStorageItemResponse
								     .withPlainInternalServerError());
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
      asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(
								     ItemStorageResource.PostItemStorageItemResponse
								     .withPlainInternalServerError());
     }*/
  }

  @Override
   public void postItemStorageItemByItemId(
        @PathParam("itemId")
        @NotNull
        String itemId,
        @QueryParam("lang")
        @DefaultValue("en")
        @Pattern(regexp = "[a-zA-Z]{2}")
        String lang, java.util.Map<String, String>okapiHeaders, io.vertx.core.Handler<io.vertx.core.AsyncResult<Response>>asyncResultHandler, Context vertxContext)
        throws Exception
    {

    }

    @Override
    public void getItemStorageItemByItemId(
        @PathParam("itemId")
        @NotNull
        String itemId,
        @QueryParam("lang")
        @DefaultValue("en")
        @Pattern(regexp = "[a-zA-Z]{2}")
        String lang, java.util.Map<String, String>okapiHeaders, io.vertx.core.Handler<io.vertx.core.AsyncResult<Response>>asyncResultHandler, Context vertxContext)
        throws Exception
    {
	Item item = new Item();
	item.setId(itemId);
	item.setTitle("Refactoring");
	item.setBarcode("31415");
	asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(ItemStorageResource.GetItemStorageItemByItemIdResponse
								       .withJsonOK(item)));
    }

    @Override
    public void putItemStorageItemByItemId(
        @PathParam("itemId")
        @NotNull
        String itemId,
        @QueryParam("lang")
        @DefaultValue("en")
        @Pattern(regexp = "[a-zA-Z]{2}")
        String lang, Item entity, java.util.Map<String, String>okapiHeaders, io.vertx.core.Handler<io.vertx.core.AsyncResult<Response>>asyncResultHandler, Context vertxContext)
        throws Exception
    {

    }

    @Override
    public void deleteItemStorageItemByItemId(
        @PathParam("itemId")
        @NotNull
        String itemId,
        @QueryParam("lang")
        @DefaultValue("en")
        @Pattern(regexp = "[a-zA-Z]{2}")
        String lang, java.util.Map<String, String>okapiHeaders, io.vertx.core.Handler<io.vertx.core.AsyncResult<Response>>asyncResultHandler, Context vertxContext)
        throws Exception
    {

    }
    
}
