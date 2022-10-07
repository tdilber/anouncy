# Anouncy

New Social Media based on Announcement with Democracy and Anonymity powered by Open Source :)

https://anouncy.com

![image](https://user-images.githubusercontent.com/10117616/190878365-dbe6a9b1-c64d-48a4-890a-363f7e6d261c.png)

Design Revision:

- Replace Mongo to Neo4j (Much fit our technical problem)

New Design Ideas:

- Use Elastic Search To seaching announce texts. Actually we have Elastic Search for logging. We can use also for this
  dev.
- We can use Minio Helm Chart for blobStorage. https://artifacthub.io/packages/helm/minio-official/minio (Note: 50 GB
  Volume Claim enough for test)
- When we start to develop BlobStorage things we need an Image CDN and a Video CDN. Maybe we will not serve first Video
  CDN because much more challenging.
- Image CDN can develop with Java on GraalVM or GoLang. We can use WebP Cli program from Google Documents.
