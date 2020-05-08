# fiskaltrust.Middleware demo (Java)
A demo application that demonstrates how to call the fiskaltrust.Middleware from Java using gRPC.

## Getting Started

### Prerequisites
In order to use this demo application, the following prerequisites are required:
- *The demo application*: Either clone and run locally, or download the latest binaries from [Releases](https://github.com/fiskaltrust/middleware-demo-java/releases)
- *The fiskaltrust.Middleware* running on your machine, which can be configured and downloaded via the fiskaltrust.Portal ([AT](https://portal.fiskaltrust.at), [DE](https://portal.fiskaltrust.de), [FR](https://portal.fiskaltrust.fr)). Start it (either by running it as a service, or with the `test.cmd` file), and let it run in the background to handle your requests.
- *Your Cashbox Id* is visible in the portal. It is also displayed in the startup console log of the Middleware. 

This example uses the .proto files of the fiskaltrust Middleware interface to automatically generate the client and the contracts at runtime via the officially suggested gRPC packages (a comprehensive tutorial and overview can be found [here](https://grpc.io/docs/tutorials/basic/java/)). The proto files are available in our [interface-doc repository](https://github.com/fiskaltrust/interface-doc/tree/master/dist/protos), and will automatically be downloaded in this sample (as described below).

To run the demo, a JDK version > 10 and Maven need to be installed. Follow the following steps:

### Running the Demo
After downloading the example zip file (`middleware-demo-java-grpc.zip`), extract the _.jar_ and the _receipt-examples_ folder to a location on your machine and start the example via the following command:

```sh
java -jar middleware-demo-java-1.0-jar-with-dependencies.jar
```

1. You will be prompted for the middleware URL and the cashbox ID.
2. The demo will show up a list of available demo receipts, pulled from the receipt-examples folder. Before executing any receipt, make sure that the SCU is initialized, by calling the *initial-operation-receipt*. 
3. To execute a receipt against the middleware, select it by its leading number and press Enter.
4. This will print the example to the command line and send it to the _Sign_ endpoint of the Middleware. After the receipt is processed, the Middleware will return the result back to the demo app, which again prints it to the console. 
5. Alternatively, a Journal request can also be executed. This is used to export different types of data from the middleware - system information, processed receipts, etc.
6. To go back to the command list, press enter

#### Manual build
This demo app uses the build system Maven, which will pull the required dependencies in case of a local build. This also includes the .proto files, which will be pulled from the [interface-doc repository](https://github.com/fiskaltrust/interface-doc/tree/master/dist/protos) and placed into `src/main/proto`.

To pull all dependencies,compile the sample, and package it to a jar file, just run
```sh
mvn package
```

### Additional information
The fiskaltrust.Middleware is written in C# and uses some language-specific functionalities that a user needs to take care of when connecting via gRPC:

Due to the binary serialization in Protobuf, `DateTime` and `decimal` (which are native types in C#) need to be converted when used outside of .NET. Thus, the `bcl.proto` is referenced in the `IPOS.proto` file. An example how to deal with these types is shown in [ProtoUtil.java](src/main/java/eu/fiskaltrust/middleware/demo/grpc/util/ProtoUtil.java).


## Documentation
The full documentation for the interface can be found on https://docs.fiskaltrust.cloud. It is activeliy maintained and developed in our [interface-doc repository](https://github.com/fiskaltrust/interface-doc). 

More information is also available after logging into the portal with a user that has the _PosCreator_ role assigned.

### Communication
The fiskaltrust.Middleware supports different communication protocols, effectively giving our customers the possibility to use it on all platforms. Hence, different protocols are recommended for different platforms. For non-windows environments, we recommend the usage of gRPC. Please have a look into our other demo repositories for alternatives, e.g. HTTP/REST or SOAP.

#### User specific protocols
With the helper topology, it is possible to solve every scenario. Please contact our support if you required assistance for a special case scenario.

## Contributions
We welcome all kinds of contributions and feedback, e.g. via Issues or Pull Requests. 

## Related resources
Our latest samples are available in the following languages:
<p align="center">
  <a href="https://github.com/fiskaltrust/middleware-demo-dotnet"><img src="https://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/C_Sharp_logo.svg/100px-C_Sharp_logo.svg.png" alt="csharp"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  <a href="https://github.com/fiskaltrust/middleware-demo-java"><img src="https://upload.wikimedia.org/wikiversity/de/thumb/b/b8/Java_cup.svg/100px-Java_cup.svg.png" alt="java"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  <a href="https://github.com/fiskaltrust/middleware-demo-node"><img src="https://upload.wikimedia.org/wikipedia/commons/thumb/d/d9/Node.js_logo.svg/100px-Node.js_logo.svg.png" alt="node"></a>
</p>

Additionally, other samples (including legacy ones) can be found in our [demo repository](https://github.com/fiskaltrust/demo).
