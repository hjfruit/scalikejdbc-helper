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

- Inherit `ArrayBinders`
  - Support `TypeBinder`
  - Support `ParameterBinderFactory`
- Inherit `JsonBinders`
  - Support `TypeBinder`
  - Support `ParameterBinderFactory`
- Inherit `bitlap.scalikejdbc.PostgresSQLSyntaxSupport`
  - Support Batch Insert
  - Support `ON CONFLICT`

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

## PostgresSQLSyntaxSupport

- Inherit `bitlap.scalikejdbc.PostgresSQLSyntaxSupport`

### On conflict
``` scala
  def upsetMetric(
    metric: MetricPO
  ): SQLUpdate =
    withSQL {
      insert
        .into(MetricPO)
        .namedValues(
          column.id            -> metric.id,
          column.displayName   -> metric.displayName,
          column.createBy      -> metric.createBy,
          column.createTime    -> metric.createTime,
          column.updateBy      -> metric.updateBy,
          column.updateTime    -> metric.updateTime
        ).onConflictUpdate(column.id)(
          column.displayName,
          column.updateBy,
          column.updateTime
      )
    }.update
```

### Batch insert

``` scala
  def batchCreateMetricRelation(entities: List[MetricRelationshipPO]): SQLUpdate =
    val values = entities.map(entity =>
      List(
        relationColumn.metricId -> entity.metricId,
        relationColumn.parentId -> entity.parentId
      )
    )

    val sql: InsertSQLBuilder =
      insert
        .into(MetricRelationshipPO)
        .columns(
          relationColumn.metricId,
          relationColumn.parentId
        )
        .multipleValuesPlus(values*)
    withSQL(sql).update
```

### Make batch insert more simplified ?

```scala
  // use autoNamedValues to generate name values. (Note: `autoNamedValues` comes from `bitlap.scalikejdbc.core`)
  val usersNameValues = users3_4.map(u =>
    autoNamedValues(User, u)
  )
  // use `autoColumns` to generate column syntax.  (Note: `autoColumns` comes from `bitlap.scalikejdbc.core`)
  val sql: InsertSQLBuilder =
    insert
      .into(User)
      .columns(autoColumns(User):_*)
      .multipleValuesPlus(usersNameValues:_*)
```
