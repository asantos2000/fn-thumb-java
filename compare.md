### Comparing thumbnailer in Node and Java9

Sources:

* Node: <https://github.com/asantos2000/fn-thumb>
* Java: <https://github.com/asantos2000/fn-thumb-java>

Deployed to FnProject local server (```$ fn start```)

```bash
$ fn routes list myapp
path		image							endpoint
/fn-thumb	adsantos/fn-thumb:0.0.13		localhost:8080/r/myapp/fn-thumb
/fn.thumb	adsantos/fn-thumb-java:0.0.6	localhost:8080/r/myapp/fn.thumb
```

## Node.JS

```
# Webhook
Request thumnbnail for:
- EventType: s3:ObjectCreated:Put
- Bucket name: images
- File name: pexels-photo.jpg
fn took: 12.269ms
Uploading new thumbail to "images-processed"
Successfully uploaded "pexels-photo-thumbnail.jpg" with md5sum "5c1a0d7b89fdee93980e05188f089d9d"
```

```
# fn run --link minio-srv < request.json
Request thumnbnail for:
- EventType: s3:ObjectCreated:Put
- Bucket name: images
- File name: pexels-photo.jpg
fn took: 8.544ms
Uploading new thumbail to "images-processed"
Successfully uploaded "pexels-photo-thumbnail.jpg" with md5sum "5c1a0d7b89fdee93980e05188f089d9d"
```

## Java

```
# Webhook
bucketName: images
objectName: pexels-photo.jpg
objectContentType: image/jpeg
start
bucketName: images-processed
small-objectName: small-pexels-photo.jpg
finish
took (ms): 4786.703273
Ok
```

```
# fn run --link minio-srv < request.json
Building image adsantos/fn-thumb-java:0.0.8
bucketName: images
objectName: pexels-photo.jpg
objectContentType: image/jpeg
start
bucketName: images-processed
small-objectName: small-pexels-photo.jpg
finish
took (ms): 4480.55853
Ok
```

In this operation, Node was **350** times faster than Java.

It use less resource than java too, to run this test, the func.yaml for java was configure to use 256MB of memory.

**java: func.yaml**

```yaml
name: adsantos/fn-thumb-java
version: 0.0.8
runtime: java
cmd: com.origoconsul.fn.thumb.App::handleRequest
build_image: fnproject/fn-java-fdk-build:jdk9-1.0.58
run_image: fnproject/fn-java-fdk:jdk9-1.0.58
type: async
memory: 256
path: /fn.thumb
``` 

With default value of 128MB, it raises java.lang.OutOfMemoryError.

```
$ fn calls list myapp

D: 01C7VTPR8687WGP00000000000
App: myapp
Route: /fn.thumb
Created At: 2018-03-05T19:37:39.334Z
Started At: 2018-03-05T19:37:39.943Z
Completed At: 2018-03-05T19:37:49.281Z
Status: error

$ fn logs get myapp 01C7VTPR8687WGP00000000000
bucketName: images
objectName: pexels-photo.jpg
objectContentType: image/jpeg
start
An error occurred in function: Java heap space
Caused by: java.lang.OutOfMemoryError: Java heap space
    at java.desktop/java.awt.image.DataBufferByte.<init>(DataBufferByte.java:92)
    at java.desktop/java.awt.image.ComponentSampleModel.createDataBuffer(ComponentSampleModel.java:439)
    at java.desktop/java.awt.image.Raster.createWritableRaster(Raster.java:1005)
    at java.desktop/javax.imageio.ImageTypeSpecifier.createBufferedImage(ImageTypeSpecifier.java:1074)
    at java.desktop/javax.imageio.ImageReader.getDestination(ImageReader.java:2877)
    at java.desktop/com.sun.imageio.plugins.jpeg.JPEGImageReader.readInternal(JPEGImageReader.java:1185)
    at java.desktop/com.sun.imageio.plugins.jpeg.JPEGImageReader.read(JPEGImageReader.java:1153)
    at java.desktop/javax.imageio.ImageIO.read(ImageIO.java:1468)
    at java.desktop/javax.imageio.ImageIO.read(ImageIO.java:1363)
    at com.origoconsul.fn.thumb.App.resizeImage(App.java:83)
    at com.origoconsul.fn.thumb.App.generateThumb(App.java:66)
    at com.origoconsul.fn.thumb.App.handleRequest(App.java:44)
    at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
    at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.base/java.lang.reflect.Method.invoke(Method.java:564)
```

Node works fine with 128MB of memory.

**Node: func.yaml**

```yaml
name: adsantos/fn-thumb
version: 0.0.13
runtime: node
entrypoint: node func.js
path: /fn-thumb
build_image: adsantos/node-py
run_image: adsantos/node-py
type: async
```
