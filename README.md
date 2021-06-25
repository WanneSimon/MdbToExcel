### Introduction

Read `.mdb` file , and convert to excel ( `.xls` , `.xlsx` ) . 

__If you can't open a big `.mdb` file , try with it.__

tech:

	1. [ucanaccess](https://github.com/andrew-nguyen/ucanaccess) ;
 	2. [EasyExcel](https://github.com/alibaba/easyexcel/) ;

<br/>

### How To Use

#### Install Project

Intall this project to your local maven repository.

``` shell
git clone https://github.com/WanneSimon/MdbToExcel.git
cd MdbToExcel
mvn install
```

<br/>

#### Import to Your Project

```xml
  <dependencies>
   <dependency>
      <groupId>cc.wanforme</groupId>
      <artifactId>MdbToExcel</artifactId>
      <version>1.0.0</version>
    </dependency>
  </dependencies>
```

<br/>

#### Examples

see [`MdbExcelExample.java `](./src/main/java/cc/wanforme/mdbexcel/example/MdbExcelExample.java)



### Demo for ucanaccess 

There's a demo on how to use driver ( `ucanaccess` ) ,  see [`MdbUcanaccessDemo.java`](./src/main/java/cc/wanforme/mdbexcel/example/MdbUcanaccessDemo.java) , 

it's not friendly to use.  

