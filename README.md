# Anouncy

New Social Media based on Announcement with Democracy and Anonymity powered by Open Source :)

https://anouncy.com

![image](https://user-images.githubusercontent.com/10117616/190878365-dbe6a9b1-c64d-48a4-890a-363f7e6d261c.png)

Design Revision:
https://stackoverflow.com/questions/13672559/saving-data-in-bash

- Replace Mongo to Neo4j (Much fit our technical problem)

TODOs:

+ Create Vote project.
+ Update to Spring Boot 3.
+ Migrate to Local communication to gRPC.
+ Install a swagger supported multi service.

- Changed to Dockerfile system to native image.
- Write Tests With test Containers.
- Check Cache ttls.
- Check comment TODOs.
- Deploy modules to test env (write helms)
- Job Create For update Region Change.
- Write the Logs.
- Connect to Kafka For votes queue for announce life cycle.
- Update the traefik custom plugin. we have some missing parts.
- Write Design Decision Document.
- Update to Readme Image.
- Write why do i doing this project. Motivation and purpose. (in Readme)

New Design Ideas:

- Use Elastic Search To seaching announce texts. Actually we have Elastic Search for logging. We can use also for this
  dev.
- We can use Minio Helm Chart for blobStorage. https://artifacthub.io/packages/helm/minio-official/minio (Note: 50 GB
  Volume Claim enough for test)
- When we start to develop BlobStorage things we need an Image CDN and a Video CDN. Maybe we will not serve first Video
  CDN because much more challenging.
- Image CDN can develop with Java on GraalVM or GoLang. We can use WebP Cli program from Google Documents.

Long Term TODOs:

- Load Test
- Autoscaling pods and autoscale nodes.
