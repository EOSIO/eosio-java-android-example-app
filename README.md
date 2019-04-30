![Java Logo](img/java-logo.png)
# EOSIO Example Java Android App

An Android app that demonstrates how developers can use [EOSIO Java](https://github.com/EOSIO/eosio-java).

All product and company names are trademarks™ or registered® trademarks of their respective holders. Use of them does not imply any affiliation with or endorsement by them.

![EOSIO Labs](https://img.shields.io/badge/EOSIO-Labs-5cb3ff.svg)

## About EOSIO Labs

EOSIO Labs repositories are experimental.  Developers in the community are encouraged to use EOSIO Labs repositories as the basis for code and concepts to incorporate into their applications. Community members are also welcome to contribute and further develop these repositories. Since these repositories are not supported by Block.one, we may not provide responses to issue reports, pull requests, updates to functionality, or other requests from the community, and we encourage the community to take responsibility for these.

<p align="center">
    <img src="img/screenshot.png" width="200">
</p>

## Contents

- [Requirements](#requirements)
- [Installation](#installation)
- [About the App](#about-the-app)
- [Contribution](#contribution)
- [License & Legal](#license)

## Requirements

* Android SDK 6.0+
* Android Studio 3.0+
* JDK 1.7

## Installation

To get the example application up and running:

1. Clone this repo: `git clone https://github.com/EOSIO/eosio-java-android-example-app.git`
1. Open the project with Android Studio.
1. Create `gradle.properties` file:

    ```java
    node_url=your node endpoint URL //mandatory
    from_account = your account //optional
    to_account = receiver account //optional
    from_account_private_key = your private key //optional
    amount = amount to transfer //optional
    memo = transaction's memo //optional
    ```
    **node_url** is mandatory for the app to point to a specific endpoint. 
    
    **from_account**, **to_account**, **from_account_private_key**, **amount** and **memo** are optional fields which will be filled to the app's form for quickly making transaction. 
1. Run the App.

## About the App

The example app is using these below libraries to communicate with EOSIO chain: 

```java
implementation 'one.block:eosiojava:0.0.1'
implementation 'one.block:eosiojavasoftkeysignatureprovider:0.0.1'
implementation 'one.block:eosiojavaandroidabieosserializationprovider:0.0.1'
implementation 'one.block:eosiojavarpcprovider:0.0.1'
```

The app can check user's balance and send transaction from the user to another user. 

The [`TransactionTask.java`](app/src/main/java/one/block/asaptestapp/TransactionTask.java) contains the sample code about how to use eosiojava libraries to send out transactions in a most basic/easiest way.

Basic steps:

1. Create serialization provider as an instant of {@link AbiEosSerializationProviderImpl} from [`eosiojavaandroidabieosserializationprovider`](https://github.com/EOSIO/eosio-java-android-abieos-serialization-provider) library.
1. Create RPC provider as an instant of [`EosioJavaRpcProviderImpl`](https://github.com/EOSIO/eosio-java-android-rpc-provider/blob/master/eosiojavarpcprovider/src/main/java/one/block/eosiojavarpcprovider/implementations/EosioJavaRpcProviderImpl.java) with an input string point to a node backend.
1. Create ABI provider as an instant of [`ABIProviderImpl`](https://github.com/EOSIO/eosio-java/blob/master/eosiojava/src/main/java/one/block/eosiojava/implementations/ABIProviderImpl.java) with instants of Rpc provider and serialization provider.
1. Create Signature provider as an instant of [` SoftKeySignatureProviderImpl`](https://github.com/EOSIO/eosio-java-softkey-signature-provider/blob/master/eosiojavasoftkeysignatureprovider/src/main/java/one/block/eosiosoftkeysignatureprovider/SoftKeySignatureProviderImpl.java) which is not recommended for production because of its simple key management.
    - Import an EOS private key which associate with sender's account which will be used to sign the transaction.
1. Create an instant of [`TransactionSession`](https://github.com/EOSIO/eosio-java/blob/master/eosiojava/src/main/java/one/block/eosiojava/session/TransactionSession.java) which is used for spawning/factory [`TransactionProcessor`](https://github.com/EOSIO/eosio-java/blob/master/eosiojava/src/main/java/one/block/eosiojava/session/TransactionProcessor.java)
1. Create an instant of [`TransactionProcessor`](https://github.com/EOSIO/eosio-java/blob/master/eosiojava/src/main/java/one/block/eosiojava/session/TransactionProcessor.java) from the instant of [`TransactionSession`](https://github.com/EOSIO/eosio-java/blob/master/eosiojava/src/main/java/one/block/eosiojava/session/TransactionSession.java) above by calling ` TransactionSession#getTransactionProcessor()` or `TransactionSession#getTransactionProcessor(Transaction)` if desire to use a preset [`Transaction`](https://github.com/EOSIO/eosio-java/blob/master/eosiojava/src/main/java/one/block/eosiojava/models/rpcProvider/Transaction.java) object.
1. Call `TransactionProcessor#prepare(List)` with a list of Actions which is desired to be sent to backend. The method will serialize the list of action to list of hex and keep them inside the list of `Transaction#getActions()`. The transaction now is ready to be signed and broadcast.
1. Call `TransactionProcessor#signAndBroadcast()` to sign the transaction inside [`TransactionProcessor`](https://github.com/EOSIO/eosio-java/blob/master/eosiojava/src/main/java/one/block/eosiojava/session/TransactionProcessor.java) and broadcast it to backend.
 

## Contribution
Check out the [Contributing](./CONTRIBUTING.md) guide.

## License
[MIT licensed](./LICENSE)

## Important

See LICENSE for copyright and license terms.  Block.one makes its contribution on a voluntary basis as a member of the EOSIO community and is not responsible for ensuring the overall performance of the software or any related applications.  We make no representation, warranty, guarantee or undertaking in respect of the software or any related documentation, whether expressed or implied, including but not limited to the warranties or merchantability, fitness for a particular purpose and noninfringement. In no event shall we be liable for any claim, damages or other liability, whether in an action of contract, tort or otherwise, arising from, out of or in connection with the software or documentation or the use or other dealings in the software or documentation.  Any test results or performance figures are indicative and will not reflect performance under all conditions.  Any reference to any third party or third-party product, service or other resource is not an endorsement or recommendation by Block.one.  We are not responsible, and disclaim any and all responsibility and liability, for your use of or reliance on any of these resources. Third-party resources may be updated, changed or terminated at any time, so the information here may be out of date or inaccurate.

Wallets and related components are complex software that require the highest levels of security.  If incorrectly built or used, they may compromise users’ private keys and digital assets. Wallet applications and related components should undergo thorough security evaluations before being used.  Only experienced developers should work with this software.
