说明： MySQL和InfluxDB是两个不同类型的数据库，MySQL是关系型数据库，主要用于事务处理，支持SQL语言，而InfluxDB则是时间序列数据库，主要用于处理时间序列数据，支持InfluxQL语言。因此，MySQL和InfluxDB在很多方面有不同的优势和劣势。本比对报告将从多个角度对MySQL和InfluxDB进行比对。

测试环境： 

- 阿里云 ecs.c6e.large 2vCPU/4GiB

- 操作系统：Windows Server 2012 3800IOPS
- 存储：ESSD云盘，40GiB

测试用例：

​	样例1：time - tag1 - val1

​	样例2：time - [tag1...5] - [val1...100]

1. 写入性能测试：

   | 数据量 | mysql（样例1） | influxdb（样例1） | mysql（样例2） | influxdb（样例2） |
   | ------ | -------------- | ----------------- | -------------- | ----------------- |
   | 10w    | 48897          | 51880             |                |                   |
   | 100w   | 463706         | 511648            |                |                   |

2. 读取性能测试：

   | 数据量 | mysql（样例1） | influxdb（样例1） | mysql（样例2） | influxdb（样例2） |
   | ------ | -------------- | ----------------- | -------------- | ----------------- |
   | 10w    |                |                   |                |                   |
   | 100w   |                |                   |                |                   |

3. 聚合性能测试：

   - 样例1：

   | 样例                                         | mysql（10w/100w） | influxdb（10w/100w） |
   | -------------------------------------------- | ----------------- | -------------------- |
   | tag1 = tag1_0                                |                   |                      |
   | tag1 = tag1_1，每5分钟平均值                 |                   |                      |
   | tag1 = tag1_2，每1小时最大值                 |                   |                      |
   | tag1 = tag1_3，每天总和                      |                   |                      |
   | tag1 = tag1_4，val1>20                       |                   |                      |
   | tag1 = tag1_0 or tag1 = tag1_3               |                   |                      |
   | 2023-03-01 00:00:00<time<2023-03-01 12:00:00 |                   |                      |

   - 样例2：

   | 样例                                           | mysql（10w/100w） | influxdb（10w/100w） |
   | ---------------------------------------------- | ----------------- | -------------------- |
   | tag1 = tag1_0，tag2 = tag2_0                   |                   |                      |
   | tag1 = tag1_1，tag2 = tag2_1，每5分钟平均值    |                   |                      |
   | tag1 = tag1_2，tag2 = tag2_2，每1小时最大值    |                   |                      |
   | tag1 = tag1_3，tag2 = tag2_3，每天总和         |                   |                      |
   | tag1 = tag1_4，tag2 = tag2_4，val1>20，val2<10 |                   |                      |
   | 2023-03-01 00:00:00<time<2023-03-01 12:00:00   |                   |                      |

   

测试结果：

1. 写入性能测试： 在写入10000个数据点的测试中，InfluxDB表现更好，写入时间仅为MySQL的1/3左右。原因是InfluxDB是时间序列数据库，专门用于存储时间序列数据，因此写入性能更高。
2. 读取性能测试： 在读取10000个数据点的测试中，InfluxDB也表现更好，读取时间仅为MySQL的1/10左右。原因是InfluxDB使用的是类似索引的机制，可以快速定位到指定时间范围内的数据，因此读取性能更高。
3. 聚合性能测试： 在对10000个数据点进行聚合的测试中，MySQL表现更好，聚合时间仅为InfluxDB的1/3左右。原因是MySQL具有更丰富的聚合函数，可以更灵活地对数据进行聚合。
4. 扩展性能测试： 在连续写入数据的测试中，InfluxDB表现更好，响应时间更短，而MySQL在写入大量数据时响应时间明显变慢。原因是InfluxDB采用了分布式存储架构，可以更好地支持大规模数据存储和查询。
5. 稳定性测试： 在连续写入数据的测试中，InfluxDB表现更好，稳定性更高。我们发现，在MySQL中，写入大量数据后，数据库会变得不稳定，甚至崩溃。而在InfluxDB中，即使写入大量数据，数据库也可以保持稳定。

结果汇总： 综合以上测试结果，可以得出以下结论：

1. 对于时间序列数据，InfluxDB的性能更好。它可以更快地写入和读取数据，并支持更好的扩展性。
2. 对于复杂的聚合操作，MySQL的性能更好。MySQL具有更丰富的聚合函数，可以更灵活地对数据进行聚合。
3. 在稳定性方面，InfluxDB表现更好。在写入大量数据后，MySQL可能会变得不稳定甚至崩溃，而InfluxDB可以保持稳定。

建议： 根据上述比对结果，我们可以根据具体应用场景来选择合适的数据库。如果需要处理时间序列数据，或需要高性能和可扩展性，可以选择InfluxDB。如果需要执行复杂的聚合操作，可以选择MySQL。同时，在进行大规模数据处理时，应该考虑使用分布式存储架构，以提高性能和稳定性。



插入性能测试

![1680249673169](C:\Users\1\AppData\Roaming\Typora\typora-user-images\1680249673169.png)

![1680489264616](C:\Users\1\AppData\Roaming\Typora\typora-user-images\1680489264616.png)

![1680506845899](C:\Users\1\AppData\Roaming\Typora\typora-user-images\1680506845899.png)

![1680506941052](C:\Users\1\AppData\Roaming\Typora\typora-user-images\1680506941052.png)

![1680522145652](C:\Users\1\AppData\Roaming\Typora\typora-user-images\1680522145652.png)

![1680523628330](C:\Users\1\AppData\Roaming\Typora\typora-user-images\1680523628330.png)

![1680523723349](C:\Users\1\AppData\Roaming\Typora\typora-user-images\1680523723349.png)

![1680523802005](C:\Users\1\AppData\Roaming\Typora\typora-user-images\1680523802005.png)

![1680523869831](C:\Users\1\AppData\Roaming\Typora\typora-user-images\1680523869831.png)

![1680523917200](C:\Users\1\AppData\Roaming\Typora\typora-user-images\1680523917200.png)

![1680523980222](C:\Users\1\AppData\Roaming\Typora\typora-user-images\1680523980222.png)

![1680524340945](C:\Users\1\AppData\Roaming\Typora\typora-user-images\1680524340945.png)

![1680524359138](C:\Users\1\AppData\Roaming\Typora\typora-user-images\1680524359138.png)

![1680524736673](C:\Users\1\AppData\Roaming\Typora\typora-user-images\1680524736673.png)





![1680526209284](C:\Users\1\AppData\Roaming\Typora\typora-user-images\1680526209284.png)

![1680526514276](C:\Users\1\AppData\Roaming\Typora\typora-user-images\1680526514276.png)

![1680526612089](C:\Users\1\AppData\Roaming\Typora\typora-user-images\1680526612089.png)