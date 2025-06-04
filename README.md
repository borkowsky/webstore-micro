# Web Store

## Functional information
> Online web store. <br>
> Products categories, subcategories, brands, tags. <br>
> Search by product name, category name, brand name. <br>
> Basket (for authorized and not authorized users). <br>
> Favorites (for authorized users). <br>
> Ordering products with delivery. <br>
> Reviews with rating and images. <br>
> Collect daily statistics for shop.

## Technical information
> Project build with microservices architecture. Auth flow realized with OAuth2 SSO. <br>
> Services registering in Spring Cloud Netflix Eureka Discovery server and can be accessible from Spring Cloud Gateway (working as OAuth2 Backend for Frontend). <br>
> The connection between services is configured using Spring Cloud OpenFeign client (sync method) and Apache Kafka (async method). <br>
> Collecting metrics realized with Grafana in Prometheus format. <br>
> Upload images realized with Google Cloud Storage service. <br>
> Soft delete for entities.

## Used stack of technologies:

* Spring boot
* Spring Cloud Config (server, client)
* OAuth2 Keycloak SSO
* Spring OAuth2 Resource server
* Spring Cloud Gateway
* Spring Cloud Discovery - Netflix Eureka (server, client)
* Spring Cloud OpenFeign
* Caffeine in-memory cache
* Apache Kafka
* Google Cloud Storage
* Grafana Metrics (Prometheus)

## Services:

1. Users
> * Addresses (read, create, update, delete). <br>
> * Basket (read, create, update, delete, sync). <br>
> * Favorites (read, create, delete). <br>
> * Me (read information about authorized user). <br>
> * Users (read: search from inner Keycloak OAuth2 storage). <br>
2. Products
> * Categories (read, create, update, delete). <br>
> * Brands (read, create, update, delete). <br>
> * Products (read, create, update, delete). <br>
> * Search (read: search by categories, brands, products). <br>
3. Orders
> * Orders (read, create, update, delete). <br>
> * Payments (read, create, update). <br>
4. Reviews
> * Reviews (read, create, delete). <br>
5. Uploads
> * Sign uploading URL (create)
> * Delete uploaded objects (delete: single and multiple)
6. Events
> * Create event (read)