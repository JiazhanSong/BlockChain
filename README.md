# BlockChain
Blockchain implementation in Java with blocks, wallets, transactions, and cryptography.

Sample output:
```
Initializing genesis block... 
Transaction added to block.
Block mined : 00000e4779a6d0911bc15b168de38974a9241323488bee5eb72d015e40d640e9
WalletA's balance is: 100.0
WalletB's balance is: 0.0

WalletA tries to send funds (50) to WalletB...
Transaction added to block.
Block mined : 0000006e8d448ddb9136f3c7a5cf78001b4df436c6d3693ba089d263c5c10ed5
WalletA's balance is: 50.0
WalletB's balance is: 50.0

WalletA tries to send more funds (500) than it has...
#ERROR - Not enough funds to send. Transaction cancelled.
Block mined : 00000724166ce0281461c999c5b23d3c3420fc56a34b042821029693d864526b
WalletA's balance is: 50.0
WalletB's balance is: 50.0

WalletB tries to send funds (20) to WalletA...
Transaction added to block.
WalletA's balance is: 70.0
WalletB's balance is: 30.0

Validating blockchain...
Blockchain is valid
```
