# Jiren
A standalone high performance on-demand file streaming service aimed for RuneTek 3. Jiren takes full advantage of the Actor Model through Akka to give users the blazing fast file streaming they need for their RuneScape game service. Besides the incredible performance Jiren offers, it is also:

- Extremely easy to use!
- Client revision independent (Though limited to RuneTek 3)
- Cache API independent

You are free to use any cache API you are used to using in your project. All Jiren requires is a simple asset loading strategy. Sample implementations for popular cache API's are provided below.

# Installation
TODO

# Getting Started
To start a new Jiren instance:

```
Jiren.create("RuneScape", 43595, 2048, 165, new OpenRSAssetLoader(cache));
```

Now what are these numbers passed as parameters?

`43595`<br>
Represents the port that Jiren will listen at for clients.

`2048`<br>
The amount of clients Jiren will serve assets to concurrently, at most. This means that it will start rejecting clients once that amount of clients are currently being served.

`165`<br>
The accepted client revision the assets are for.

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
As Jiren is completely standalone, it can be deployed anywhere. However, it is NOT recommended to have Jiren run on the same application instance as your game service. Streaming files creates a lot of garbage that could have an impact on your game service and should therefore be separated. Note that it is possible for Jiren to act as a mere backend service in your distributed system where a proxy server is connected to Jiren and forwards data to the actual clients.