# README

- Install [sbt](https://www.scala-sbt.org/1.x/docs/Setup.html) (Scala Build Tool)
- Compile `sbt clean compile`
- Test `sbt test`  
- Run `sbt run`


Example commands (GET, POST, PUT):

- `POST/PUT`
curl  -H "Content-Type: application/json" -X PUT http://localhost:8080/transactionservice/transaction/10 -d '{ "type":"cars","amount" : 5000}'


- `POST/PUT`
curl  -H "Content-Type: application/json" -X PUT http://localhost:8080/transactionservice/transaction/11 -d '{ "type":"shopping","amount" : 10000, "parent_id":10}'


- `GET`
curl http://localhost:8080/transactionservice/types/cars


- `GET`
curl  http://localhost:8080/transactionservice/sum/10


- `GET`
curl  http://localhost:8080/transactionservice/sum/11  