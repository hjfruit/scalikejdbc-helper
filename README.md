# scalikejdbc-binders

![CI][Badge-CI]  [![codecov][Badge-Codecov]][Link-Codecov]   [![Nexus (Snapshots)][Badge-Snapshots]][Link-Snapshots] 

[Badge-CI]: https://github.com/bitlap/scalikejdbc-binders/actions/workflows/ScalaCI.yml/badge.svg
[Badge-Codecov]: https://codecov.io/gh/bitlap/scalikejdbc-binders/branch/master/graph/badge.svg?token=IA596YRTOT
[Badge-Snapshots]: https://img.shields.io/nexus/s/org.bitlap/scalikejdbc-binders-postgres_3?server=https%3A%2F%2Fs01.oss.sonatype.org

[Link-Codecov]: https://codecov.io/gh/bitlap/scalikejdbc-binders
[Link-Snapshots]: https://s01.oss.sonatype.org/content/repositories/snapshots/org/bitlap/scalikejdbc-binders-postgres_3

----

# array
| postgres type | scala type | collections         |
|---------------|------------|---------------------|
| varchar       | String     | List,Set,Seq,Vector |
| integer       | Int        | List,Set,Seq,Vector |
| decimal       | BigDecimal | List,Set,Seq,Vector |

```scala
    final case class UserRepository() extends ArrayBinders
 
    object UserRepositoryTable extends SQLSyntaxSupport[UserRepository] with ArrayBinders:
        // using ParameterBinderFactory
        def apply(up: ResultName[UserRepository])(rs: WrappedResultSet)(using connection: Connection): UserRepository =
          autoConstruct(rs, up) 
    
    
    object UserRepositoryTable extends SQLSyntaxSupport[UserRepository] with ArrayBinders:
        // using TypeBinder
        given Function1[Array[Any] , List[String]] = ???  // in scope
```

# json

```scala

    final case class UserRepository() extends JsonBinders
    // json string to map 
        given Function1[String , Map[String, String]] = ???
    
```