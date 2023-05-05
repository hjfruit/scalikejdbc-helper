# scalikejdbc-helper

![CI][Badge-CI]  [![ScalaIndex][ScalaIndex-Releases]][ScalaIndex-Link] 

[Badge-CI]: https://github.com/bitlap/scalikejdbc-helper/actions/workflows/ScalaCI.yml/badge.svg
[ScalaIndex-Releases]: https://index.scala-lang.org/bitlap/scalikejdbc-helper/scalikejdbc-helper-postgres/latest-by-scala-version.svg?platform=jvm
[ScalaIndex-Link]: https://index.scala-lang.org/bitlap/scalikejdbc-helper/scalikejdbc-helper-postgres

----

# postgres

- Dependency

```scala
"org.bitlap" %% "scalikejdbc-helper-postgres" % "<version>"
```

- Inherit `bitlap.scalikejdbc.binders.AllBinders`
  - Support `TypeBinder` for array, scala3 enumeration (without params)
  - Support `ParameterBinderFactory` for scala collections and scala3 enumeration 
- Inherit `bitlap.scalikejdbc.PostgresSQLSyntaxSupport`
  - Support Batch Insert
  - Support `ON CONFLICT`

## Array
### ParameterBinderFactory
| scala collections   | scala type | postgres type |
|---------------------|------------|---------------|
| List,Set,Seq,Vector | String     | varchar       |
| List,Set,Seq,Vector | Int        | integer       |
| List,Set,Seq,Vector | BigDecimal | decimal       |
| List,Set,Seq,Vector | Long       | bigint        |

### TypeBinder
| postgres type | scala type | supported collections |
|---------------|------------|-----------------------|
| varchar       | String     | List,Set,Seq,Vector   |
| integer       | Int        | List,Set,Seq,Vector   |
| decimal       | BigDecimal | List,Set,Seq,Vector   |
| bigint        | Long       | List,Set,Seq,Vector   |

## Json
### ParameterBinderFactory
| scala type                     | postgres type | required implicit mapping function       |
|--------------------------------|---------------|------------------------------------------|
| scala.collection.immutable.Map | json/jsonb    | scala.collection.immutable.Map => String |

### TypeBinder
| postgres type | scala type                     | required implicit mapping function       |
|---------------|--------------------------------|------------------------------------------|
 | jsonb/json    | scala.collection.immutable.Map | String => scala.collection.immutable.Map |

## Enum

### ParameterBinderFactory
| scala type | postgres type | 
|------------|---------------|
| enum       | smallint      |

### TypeBinder
| postgres type | scala type | 
|---------------|------------|
| smallint      | enum       |

## Manual definition
```scala
given ParameterBinderFactory[List[Short]] = DeriveParameterBinder.array[Short, List](ObjectType.Short, _.toArray)

given ParameterBinderFactory[Map[String, String]] = DeriveParameterBinder.json[Map[String, String]](toJson)

given TypeBinder[List[BigDecimal]] = DeriveTypeBinder.array[BigDecimal, List](_.toList.map(s => BigDecimal(s.toString)), Nil)
```

## How to use enumeration to optimize storage space?

```scala
// We will derive a typeclass `IntToEnum` to generate `fromOrdinal` in static state
// If it cannot find a TypeBinder[TestEnum], try to manually add it， such as: `enum TestEnum derives IntToEnum` 
enum TestEnum:
  case Enum1 extends TestEnum
  case Enum2 extends TestEnum

final case class EnumEntity(id: TestEnum)
```

> The ordinal of the enumeration starts from 0, and changes in order will affect the ordinal
 
**This uses the smallest storage space**
```sql
create table testdb.t_enum
(
    id smallint
);
```

**This has higher readability**
```scala
  def insertEnum(
    e: EnumEntity
  ): SQLUpdate =
    withSQL {
      insert
        .into(EnumTable)
        .namedValues(
          enumColumn.id -> e.id
        )
    }.update

  def queryEnum()(using a: AutoSession = AutoSession): Option[EnumEntity] =
    withSQL {
      select.from(EnumTable as e)
    }.map(EnumTable(e.resultName)).single.apply()
```

## PostgresSQLSyntaxSupport

[DSL Extension](PG.md)