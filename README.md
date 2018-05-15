# Jiren
A standalone high performance on-demand file streaming service aimed for RuneTek 3. Jiren takes full advantage of the Actor Model through Akka to give users the blazing fast file streaming they need for their RuneScape game service. Besides the incredible performance Jiren offers, it is also:

- Extremely easy to use!
- Client revision independent (Though limited to RuneTek 3)
- Cache API independent

You are free to use any cache API you are used to using in your project. All Jiren requires is a simple asset loading strategy. Sample implementations for popular cache API's are provided below.

# Installation
TODO

# Getting Started
TODO

## Asset Loaders
OpenRS:

```
final class OpenRSAssetLoader(cache: Cache) extends AssetLoader {
  override def load(archive: Int, file: Int) =
    if (archive == 255 && file == 255) {
      cache.createChecksumTable().encode()
    } else {
      cache.getStore.read(archive, file)
    }
}
```

## Deployment
As Jiren is completely standalone, it can be deployed anywhere. However, this means that Jiren can not run on the same instance as your game service. Clients must either directly connect to this service or a proxy server (which is built by you) that communicates with Jiren.