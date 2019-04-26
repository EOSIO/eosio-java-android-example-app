# EOSIO Example Java Android App

An Android app that demonstrates how developers can use [EOSIO Java](https://github.com/EOSIO/eosio-java).

![EOSIO Labs](https://img.shields.io/badge/EOSIO-Labs-5cb3ff.svg)

## About EOSIO Labs

EOSIO Labs repositories are experimental.  Developers in the community are encouraged to use EOSIO Labs repositories as the basis for code and concepts to incorporate into their applications. Community members are also welcome to contribute and further develop these repositories. Since these repositories are not supported by Block.one, we may not provide responses to issue reports, pull requests, updates to functionality, or other requests from the community, and we encourage the community to take responsibility for these.

## Usage

The example app is using these below libraries to communicate with EOSIO chain: 

```java
implementation 'one.block:eosiojava:0.0.1'
implementation 'one.block:eosiojavasoftkeysignatureprovider:0.0.1'
implementation 'one.block:eosiojavaandroidabieosserializationprovider:0.0.1'
implementation 'one.block:eosiojavarpcprovider:0.0.1'
```

There are some predefined properties need to be filled to run the app in **eosio.properties**: 

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


## Contribution
Check out the [Contributing](./CONTRIBUTING.md) guide.

## License
[MIT licensed](./LICENSE)

## Important

See LICENSE for copyright and license terms.  Block.one makes its contribution on a voluntary basis as a member of the EOSIO community and is not responsible for ensuring the overall performance of the software or any related applications.  We make no representation, warranty, guarantee or undertaking in respect of the software or any related documentation, whether expressed or implied, including but not limited to the warranties or merchantability, fitness for a particular purpose and noninfringement. In no event shall we be liable for any claim, damages or other liability, whether in an action of contract, tort or otherwise, arising from, out of or in connection with the software or documentation or the use or other dealings in the software or documentation.  Any test results or performance figures are indicative and will not reflect performance under all conditions.  Any reference to any third party or third-party product, service or other resource is not an endorsement or recommendation by Block.one.  We are not responsible, and disclaim any and all responsibility and liability, for your use of or reliance on any of these resources. Third-party resources may be updated, changed or terminated at any time, so the information here may be out of date or inaccurate.

Wallets and related components are complex software that require the highest levels of security.  If incorrectly built or used, they may compromise users’ private keys and digital assets. Wallet applications and related components should undergo thorough security evaluations before being used.  Only experienced developers should work with this software.
