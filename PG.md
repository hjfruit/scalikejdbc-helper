PostgresSQLSyntaxSupport
---

- Inherit `bitlap.scalikejdbc.PostgresSQLSyntaxSupport`

## On conflict
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

## Batch insert

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

## Make batch insert more simplified ?

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