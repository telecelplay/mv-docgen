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



```mermaid
erDiagram
liquimartproductreview }0--|| wallet : has
liquimartproductreview {
ENTITY buyerwalletaddress
LONG_TEXT comments
STRING productcode
DATE purchasedate
LONG rating
STRING reviewdate
STRING sellerwalletaddress
}
purchaseorder }0--|| wallet : has
purchaseorder }0--|| grouppurchase : has
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
grouppurchase }0--|| wallet : has
grouppurchase }0--|| purchaseorder : has
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
cart }0--|| wallet : has
cart }0--|| grouppurchase : has
cart ||--o{ purchaseorderline : has
cart {
DOUBLE amount
DATE creationdate
ENTITY customer
ENTITY grouppurchase
CHILD_ENTITY orderlines
}
product }0--|| merchant : has
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
merchant }0--|| wallet : has
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
