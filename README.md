# Jiren
A standalone high performance on-demand file streaming service aimed for RuneTek 3. Jiren takes full advantage of the Actor Model through Akka to give users the blazing fast file streaming they need for their RuneScape game service. Besides the incredible performance Jiren offers, it is also extremely easy to use and is cache API independent. This means that you simply provide a cache API you would like to use and the corresponding asset loader. Sample asset loader implementations using the most popular cache API's can be found below.

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