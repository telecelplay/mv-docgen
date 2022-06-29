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
CustomFieldTypeEnum.ENTITY buyerwalletaddress
CustomFieldTypeEnum.LONG_TEXT comments
CustomFieldTypeEnum.STRING productcode
CustomFieldTypeEnum.DATE purchasedate
CustomFieldTypeEnum.LONG rating
CustomFieldTypeEnum.STRING reviewdate
CustomFieldTypeEnum.STRING sellerwalletaddress
}
purchaseorder }0--|| wallet : has
purchaseorder }0--|| grouppurchase : has
purchaseorder ||--o{ purchaseorderline : has
purchaseorder {
CustomFieldTypeEnum.DOUBLE amount
CustomFieldTypeEnum.STRING cashbacktransactionid
CustomFieldTypeEnum.DATE creationdate
CustomFieldTypeEnum.ENTITY customer
CustomFieldTypeEnum.ENTITY grouppurchase
CustomFieldTypeEnum.STRING orderid
CustomFieldTypeEnum.CHILD_ENTITY orderlines
CustomFieldTypeEnum.STRING paymenttransaction
CustomFieldTypeEnum.STRING productid
CustomFieldTypeEnum.STRING purchasetransactionid
CustomFieldTypeEnum.LONG quantity
}
grouppurchase }0--|| wallet : has
grouppurchase }0--|| purchaseorder : has
grouppurchase {
CustomFieldTypeEnum.ENTITY ackmembers
CustomFieldTypeEnum.STRING cashbacktransactions
CustomFieldTypeEnum.DATE creationdate
CustomFieldTypeEnum.ENTITY creator
CustomFieldTypeEnum.LONG currentmemberscount
CustomFieldTypeEnum.STRING discountid
CustomFieldTypeEnum.LONG discountminparticipants
CustomFieldTypeEnum.DOUBLE discountpercent
CustomFieldTypeEnum.DATE expirydate
CustomFieldTypeEnum.DATE lastupdate
CustomFieldTypeEnum.ENTITY memberpurchases
CustomFieldTypeEnum.ENTITY members
CustomFieldTypeEnum.STRING name
CustomFieldTypeEnum.ENTITY paidmembers
CustomFieldTypeEnum.LONG paidmemberscount
CustomFieldTypeEnum.STRING productid
CustomFieldTypeEnum.STRING productname
CustomFieldTypeEnum.LONG purchasedquantity
CustomFieldTypeEnum.LIST status
}
cart }0--|| wallet : has
cart }0--|| grouppurchase : has
cart ||--o{ purchaseorderline : has
cart {
CustomFieldTypeEnum.DOUBLE amount
CustomFieldTypeEnum.DATE creationdate
CustomFieldTypeEnum.ENTITY customer
CustomFieldTypeEnum.ENTITY grouppurchase
CustomFieldTypeEnum.CHILD_ENTITY orderlines
}
product }0--|| merchant : has
product {
CustomFieldTypeEnum.LONG bestseller
CustomFieldTypeEnum.LONG categid
CustomFieldTypeEnum.STRING code
CustomFieldTypeEnum.LONG costcurrencyid
CustomFieldTypeEnum.DATE createdate
CustomFieldTypeEnum.LONG currencyid
CustomFieldTypeEnum.STRING displayname
CustomFieldTypeEnum.LONG groupdiscountid
CustomFieldTypeEnum.LONG id
CustomFieldTypeEnum.DOUBLE listprice
CustomFieldTypeEnum.LONG locationid
CustomFieldTypeEnum.ENTITY merchant
CustomFieldTypeEnum.STRING name
}
merchant }0--|| wallet : has
merchant {
CustomFieldTypeEnum.SECRET accountcountercode
CustomFieldTypeEnum.STRING accountfirstname
CustomFieldTypeEnum.STRING accountlastname
CustomFieldTypeEnum.SECRET accountribkey
CustomFieldTypeEnum.STRING bankaccountnumber
CustomFieldTypeEnum.STRING bankcode
CustomFieldTypeEnum.SECRET cardcvc
CustomFieldTypeEnum.STRING cardexpirymonth
CustomFieldTypeEnum.STRING cardexpiryyear
CustomFieldTypeEnum.STRING cardholderfirstname
CustomFieldTypeEnum.STRING cardholderlastname
CustomFieldTypeEnum.SECRET cardnumber
CustomFieldTypeEnum.STRING cardtype
CustomFieldTypeEnum.LONG id
CustomFieldTypeEnum.STRING name
CustomFieldTypeEnum.STRING orangephonenumber
CustomFieldTypeEnum.ENTITY wallet
}
purchaseorderline {
CustomFieldTypeEnum.STRING currency
CustomFieldTypeEnum.DOUBLE price
CustomFieldTypeEnum.STRING productid
CustomFieldTypeEnum.STRING productimageid
CustomFieldTypeEnum.STRING productname
CustomFieldTypeEnum.LONG quantity
CustomFieldTypeEnum.DOUBLE unitprice
}
```
