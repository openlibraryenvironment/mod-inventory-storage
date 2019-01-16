package org.folio.rest.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.folio.rest.annotations.Validate;
import org.folio.rest.jaxrs.model.Item;
import org.folio.rest.jaxrs.model.Items;
import org.folio.rest.jaxrs.model.Status;
import org.folio.rest.jaxrs.resource.ItemStorage;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.persist.Criteria.Limit;
import org.folio.rest.persist.Criteria.Offset;
import org.folio.rest.persist.cql.CQLWrapper;
import org.folio.rest.tools.utils.TenantTool;
import org.z3950.zing.cql.cql2pgjson.CQL2PgJSON;
import org.z3950.zing.cql.cql2pgjson.FieldException;

/**
 * CRUD for Item.
 */
public class ItemStorageAPI implements ItemStorage {

  static final String ITEM_TABLE = "item";
  private static final String ITEM_MATERIALTYPE_VIEW = "items_mt_view";

  private static final Logger log = LoggerFactory.getLogger(ItemStorageAPI.class);
  private static final String DEFAULT_STATUS_NAME = "Available";

  @Validate
  @Override
  public void getItemStorageItems(
    int offset,
    int limit,
    String query,
    String lang,
    Map<String, String> okapiHeaders,
    Handler<AsyncResult<Response>> asyncResultHandler,
    Context vertxContext) {

    try {
      vertxContext.runOnContext(v -> {
        try {
          PostgresClient postgresClient = StorageHelper.postgresClient(vertxContext, okapiHeaders);

          String[] fieldList = {"*"};
          
          CQL2PgJSON cql2pgJson = new CQL2PgJSON("item.jsonb");
          CQLWrapper cql = new CQLWrapper(cql2pgJson, query)
            .setLimit(new Limit(limit))
            .setOffset(new Offset(offset));

          postgresClient.get("item", Item.class, fieldList, cql, true, false,
            reply -> {
              try {

                if(reply.succeeded()) {
                  List<Item> items = reply.result().getResults();

                  Items itemList = new Items();
                  itemList.setItems(items);
                  itemList.setTotalRecords(reply.result().getResultInfo().getTotalRecords());

                  asyncResultHandler.handle(Future.succeededFuture(
                    GetItemStorageItemsResponse.
                      respond200WithApplicationJson(itemList)));
                }
                else {
                  asyncResultHandler.handle(Future.succeededFuture(
                    GetItemStorageItemsResponse.
                      respond500WithTextPlain(reply.cause().getMessage())));
                }
              } catch (Exception e) {
                if(e.getCause() != null && e.getCause().getClass().getSimpleName().contains("CQLParseException")) {
                  asyncResultHandler.handle(Future.succeededFuture(
                    GetItemStorageItemsResponse.respond400WithTextPlain(
                      "CQL Parsing Error for '" + query + "': " + e.getLocalizedMessage())));
                }
                else {
                  asyncResultHandler.handle(Future.succeededFuture(
                    GetItemStorageItemsResponse.
                      respond500WithTextPlain("Error")));
                }
              }
            });
        }
        catch (IllegalStateException e) {
          asyncResultHandler.handle(Future.succeededFuture(
            GetItemStorageItemsResponse.respond500WithTextPlain(
              "CQL State Error for '" + query + "': " + e.getLocalizedMessage())));
        }
        catch (Exception e) {
          if(e.getCause() != null && e.getCause().getClass().getSimpleName().contains("CQLParseException")) {
            asyncResultHandler.handle(Future.succeededFuture(
              GetItemStorageItemsResponse.respond400WithTextPlain(
              "CQL Parsing Error for '" + query + "': " + e.getLocalizedMessage())));
          } else {
            asyncResultHandler.handle(Future.succeededFuture(
              GetItemStorageItemsResponse.respond500WithTextPlain("Error")));
          }
        }
      });
    } catch (Exception e) {
      if(e.getCause() != null && e.getCause().getClass().getSimpleName().contains("CQLParseException")) {
        asyncResultHandler.handle(Future.succeededFuture(
          GetItemStorageItemsResponse.respond400WithTextPlain(
            "CQL Parsing Error for '" + query + "': " + e.getLocalizedMessage())));
      } else {
        asyncResultHandler.handle(Future.succeededFuture(
          GetItemStorageItemsResponse.respond500WithTextPlain("Error")));
      }
    }
  }

  @Validate
  @Override
  public void postItemStorageItems(
      String lang, Item entity, Map<String, String> okapiHeaders,
      Handler<AsyncResult<Response>> asyncResultHandler,
      Context vertxContext) {

    if (entity.getStatus() == null) {
      entity.setStatus(new Status().withName(DEFAULT_STATUS_NAME));
    }
    StorageHelper.post(ITEM_TABLE, entity, okapiHeaders, vertxContext,
        PostItemStorageItemsResponse.class, asyncResultHandler);
  }

  @Validate
  @Override
  public void getItemStorageItemsByItemId(
      String itemId, String lang, java.util.Map<String, String> okapiHeaders,
      io.vertx.core.Handler<io.vertx.core.AsyncResult<Response>> asyncResultHandler,
      Context vertxContext) {

    StorageHelper.getById(ITEM_TABLE, Item.class, itemId, okapiHeaders, vertxContext,
        GetItemStorageItemsByItemIdResponse.class, asyncResultHandler);
  }

  @Validate
  @Override
  public void deleteItemStorageItems(
    String lang, Map<String, String> okapiHeaders,
    Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {

    String tenantId = TenantTool.tenantId(okapiHeaders);
    PostgresClient postgresClient = StorageHelper.postgresClient(vertxContext, okapiHeaders);

    postgresClient.mutate(String.format("TRUNCATE TABLE %s_%s.item", tenantId, "mod_inventory_storage"),
        reply -> {
          if (reply.succeeded()) {
            asyncResultHandler.handle(Future.succeededFuture(
                DeleteItemStorageItemsResponse.noContent()
                .build()));
          } else {
            log.error(reply.cause().getMessage(), reply.cause());
            asyncResultHandler.handle(Future.succeededFuture(
                DeleteItemStorageItemsResponse.
                respond500WithTextPlain(reply.cause().getMessage())));
          }
        });
  }

  @Validate
  @Override
  public void putItemStorageItemsByItemId(
      String itemId, String lang, Item entity, java.util.Map<String, String> okapiHeaders,
      io.vertx.core.Handler<io.vertx.core.AsyncResult<Response>> asyncResultHandler,
      Context vertxContext) {

    StorageHelper.put(ITEM_TABLE, entity, itemId, okapiHeaders, vertxContext,
        PutItemStorageItemsByItemIdResponse.class, asyncResultHandler);
  }

  @Validate
  @Override
  public void deleteItemStorageItemsByItemId(
      String itemId, String lang, java.util.Map<String, String> okapiHeaders,
      io.vertx.core.Handler<io.vertx.core.AsyncResult<Response>> asyncResultHandler,
      Context vertxContext) {

    StorageHelper.deleteById(ITEM_TABLE, itemId, okapiHeaders, vertxContext,
        DeleteItemStorageItemsByItemIdResponse.class, asyncResultHandler);
  }
}
