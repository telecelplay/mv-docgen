mv-docgen
=========
doc generator module - install this module and generate/update documentation (README.md) of any installed module by passing module code in postman endpoint call

Rest Service
------------
| Name         | Endpoint URL                      | Method | Description                                               |
| ------------ | --------------------------------- | ------ | --------------------------------------------------------- |
| docGenerator | /rest/module/{moduleCode}/doc/gen | POST   | Doc Generator Endpoint - to generate module documentation |

* Input Fields:

| Object     | Type   | Default Value | List Options | Obs / Conditions |
| ---------- | ------ | ------------- | ------------ | ---------------- |
| moduleCode | String |               |              |                  |

* Output Fields:

| Object | Type   | Description |
| ------ | ------ | ----------- |
| result | Object |             |

### Meveo Function
| Type           | Name                       | Path                                                                                                                                                   | Description          |
| -------------- | -------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------ | -------------------- |
| Meveo Function | org.meveo.doc.DocGenScript | [master/facets/java/org/meveo/doc/DocGenScript.java](https://github.com/telecelplay/mv-docgen/blob/master/facets/java/org/meveo/doc/DocGenScript.java) | Doc Generator Script |

Postman Tests 
--------------
| Path                                                                                                                                                                             |
| -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| [master/facets/postman/Doc_Generator_API.postman_collection.json](https://github.com/telecelplay/mv-docgen/blob/master/facets/postman/Doc_Generator_API.postman_collection.json) |


ERD Diagram GroupBuying
-----------


```mermaid
erDiagram
liquimartproductreview }o--|| wallet : has
liquimartproductreview {
ENTITY buyerwalletaddress
LONG_TEXT comments
STRING productcode
DATE purchasedate
LONG rating
STRING reviewdate
STRING sellerwalletaddress
}
purchaseorder }o--|| wallet : has
purchaseorder }o--|| grouppurchase : has
purchaseorder ||--o{ purchaseorderline : has
purchaseorder {
DOUBLE amount
STRING cashbacktransactionid
DATE creationdate
ENTITY customer
ENTITY grouppurchase
STRING orderid
CHILD_ENTITY orderlines
STRING paymenttransaction
STRING productid
STRING purchasetransactionid
LONG quantity
}
grouppurchase }o--|| wallet : has
grouppurchase }o--|| purchaseorder : has
grouppurchase {
ENTITY ackmembers
STRING cashbacktransactions
DATE creationdate
ENTITY creator
LONG currentmemberscount
STRING discountid
LONG discountminparticipants
DOUBLE discountpercent
DATE expirydate
DATE lastupdate
ENTITY memberpurchases
ENTITY members
STRING name
ENTITY paidmembers
LONG paidmemberscount
STRING productid
STRING productname
LONG purchasedquantity
LIST status
}
cart }o--|| wallet : has
cart }o--|| grouppurchase : has
cart ||--o{ purchaseorderline : has
cart {
DOUBLE amount
DATE creationdate
ENTITY customer
ENTITY grouppurchase
CHILD_ENTITY orderlines
}
product }o--|| merchant : has
product {
LONG bestseller
LONG categid
STRING code
LONG costcurrencyid
DATE createdate
LONG currencyid
STRING displayname
LONG groupdiscountid
LONG id
DOUBLE listprice
LONG locationid
ENTITY merchant
STRING name
}
merchant }o--|| wallet : has
merchant {
SECRET accountcountercode
STRING accountfirstname
STRING accountlastname
SECRET accountribkey
STRING bankaccountnumber
STRING bankcode
SECRET cardcvc
STRING cardexpirymonth
STRING cardexpiryyear
STRING cardholderfirstname
STRING cardholderlastname
SECRET cardnumber
STRING cardtype
LONG id
STRING name
STRING orangephonenumber
ENTITY wallet
}
purchaseorderline {
STRING currency
DOUBLE price
STRING productid
STRING productimageid
STRING productname
LONG quantity
DOUBLE unitprice
}
```

ERD Diagram Liquichain
-----------

```mermaid
erDiagram
block {
LONG blocknumber
DATE creationdate
STRING hash
STRING parenthash
LONG size
}
verifiedemail {
STRING email
BOOLEAN verified
STRING walletid
}
torrentannounce }o--|| liquichainapp : has
torrentannounce }o--|| wallet : has
torrentannounce {
DATE anouncedate
ENTITY application
LONG downloaded
STRING infohash
STRING ip
DATE lastannouncedate
DOUBLE latitude
LONG left
DOUBLE liveness
DOUBLE longitude
STRING peerid
LONG port
LIST status
LONG uploaded
STRING url
ENTITY wallet
}
wallet }o--|| liquichainapp : has
wallet }o--|| verifiedemail : has
wallet }o--|| grouppurchase : has
wallet }o--|| verifiedphonenumber : has
wallet {
STRING accounthash
ENTITY application
STRING applicationinstanceuuid
STRING balance
ENTITY emailaddress
ENTITY grouppurchases
STRING hexhash
STRING keypair
LONG lastprivateinforequest
STRING name
ENTITY phonenumber
STRING privatekey
LONG_TEXT publicinfo
STRING publickey
BOOLEAN verified
}
liquichainapp {
DATE creationdate
STRING description
STRING hexcode
STRING iconurl
STRING name
STRING previoushash
STRING registrationrules
STRING shortcode
STRING upgraderules
STRING version
}
splashscreen {
TEXT_AREA content
BINARY image
LONG order
STRING title
}
transaction {
STRING blockhash
STRING blocknumber
DATE creationdate
STRING data
STRING fromhexhash
STRING gaslimit
STRING gasprice
STRING hexhash
STRING nodesignature
STRING nonce
STRING r
STRING s
STRING signedhash
STRING tohexhash
LONG transactionindex
STRING type
STRING v
STRING value
}
verifiedphonenumber {
STRING phonenumber
BOOLEAN verified
STRING walletid
}
```
