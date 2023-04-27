# scalikejdbc-helper

![CI][Badge-CI]  [![Nexus][Badge-Releases]][Link-Releases] 

[Badge-CI]: https://github.com/bitlap/scalikejdbc-helper/actions/workflows/ScalaCI.yml/badge.svg
[Badge-Releases]: https://img.shields.io/nexus/r/org.bitlap/scalikejdbc-helper-postgres_3?server=https%3A%2F%2Fs01.oss.sonatype.org
[Link-Releases]: https://s01.oss.sonatype.org/content/repositories/releases/org/bitlap/scalikejdbc-helper-postgres_3/

----

# postgres

- Dependency

```scala
"org.bitlap" %% "scalikejdbc-helper-postgres" % <version>
```

- Inherit `bitlap.scalikejdbc.binders.AllBinders`
  - Support `TypeBinder`
  - Support `ParameterBinderFactory`
  - Support Batch Insert
  - Support `ON CONFLICT`
  - Support Scala3 enum (without params)

## ParameterBinderFactory array
| postgres type | scala type | supported collections |
|---------------|------------|-----------------------|
| varchar       | String     | List,Set,Seq,Vector   |
| integer       | Int        | List,Set,Seq,Vector   |
| decimal       | BigDecimal | List,Set,Seq,Vector   |
| bigint        | Long       | List,Set,Seq,Vector   |

## TypeBinder array
| postgres type | scala type | supported collections |
|---------------|------------|-----------------------|
| varchar       | String     | List,Set,Seq,Vector   |
| integer       | Int        | List,Set,Seq,Vector   |
| decimal       | BigDecimal | List,Set,Seq,Vector   |
| bigint        | Long       | List,Set,Seq,Vector   |

## ParameterBinderFactory json
| postgres type | scala type | required implicit mapping function |
|---------------|------------|------------------------------------|
| json          | `T`        | `String => T`                      |

## TypeBinder json
| postgres type | scala type | required implicit mapping function |
|---------------|------------|------------------------------------|
| json          | `T`        | `T => String`                      |

## Manual definition
```scala
given ParameterBinderFactory[List[Short]] = DeriveParameterBinder.array[Short, List](ObjectType.Short, _.toArray)

given ParameterBinderFactory[Map[String, String]] = DeriveParameterBinder.json[Map[String, String]](toJson)

given TypeBinder[List[BigDecimal]] = DeriveTypeBinder.array[BigDecimal, List](_.toList.map(s => BigDecimal(s.toString)), Nil)
```

## Scala3 enum

```scala
// We will derive a typeclass `IntToEnum` to generate `fromOrdinal` in static state
enum TestEnum:
  case Enum1 extends TestEnum
  case Enum2 extends TestEnum

final case class EnumEntity(id: TestEnum)
```

**Enumeration uses less space and has good readability**
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