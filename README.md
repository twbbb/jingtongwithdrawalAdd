socket监听井通地址。定时任务复查

这个服务，主要是监听钱包，并且将对应数据放入数据库。

application.properties里面subscribe_address为监听的钱包地址。

WebSocketStartComponent启动socket，用于监听钱包，并且将入账数据，插入表withdrawal。状态待提现。并且将hash放入缓存WithdrawalData. withdrawalSet

通过定时任务TransactionLogTask，获取钱包交易记录，
每分钟执行一次，由于数据是按时间排好序的，只需要取到数据库记录中最后的hash。

如果交易记录是入账数据，会设置状态为待比对，否则设置为无需比对。

待比对记录，全部放入tobeCheckTranList，然后去检查socket的数据withdrawalSet，是否存在，如果存在，会标记为比对成功，
如果不存在，会标记为3.socket没有，待添加。并且放回tobeCheckTranList(以防特殊情况socket收到数据比定时任务慢)
如果不存在，并且状态是3.socket没有，待添加，则会在withdrawal插入一条记录，数据来源是定时任务。

数据库，表withdrawal，hash唯一约束
