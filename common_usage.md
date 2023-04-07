### monibot-base-api REST API 接口文档说明

#### 1.ErrCode状态码(Code)定义：

   0：操作成功

   1：用户名或密码错误

   8：服务器内部错误

   9：当前用户不允许此access_type登录

   10：由于用户已在别处登录，所以token已经失效

   11：token不存在或者已经失效

   12：没有权限

   13：非法的参数

   14：服务未授权或者已过期

   15：微信登录失败

   16：httphead中不包含access_type或者access_type的值错误

   17：当前用户已被禁用

   18：手机号，验证码正确，但是相应的用户不存在

   19：手机号或者验证码错误

   20：系统初始化数据错误

   21：不包含app_key(app_secret)或者错误的app_key(app_secret)

   22：Redis事务错误

   23：资源限制

   24：第三方服务调用失败

   25：访问过于频繁

   26：文件资源未找到

   28：文件格式错误

#### 2.权限说明：

   PUBLIC:任何人(包括未登录的人)皆拥有此接口的访问权限，比如ApiVersion等

   LOGGED:任何登录的用户皆拥有此接口的访问权限,比如GetMyInfo,ChangeMyPassword接口等

   其他：必须在相应的公司/资源中具有相应的操作权限

   **继承**：用于在本公司或者本公司的父级公司中具有权限都认为具有了相应的权限。
   
   系统权限中标明**继承**的支持继承，项目权限和设备权限不支持继承

#### 3.参数和响应说明
   参数字段，没有特别注明，不允许为空。
   如果参数只有一个字段，仍需包装成JSON对象。比如接口只需一个companyID参数，则需要提供的参数为:{"companyID":1}。
   如果返回结果只有一个字段，则使用统一格式的data字段。
   本服务所有接口无参数调用采用GET请求，有参数调用采用POST请求。
   
   **注意**：不允许为空却传入空的参数以及非法的参数会导致400状态码。

#### 4.JSON序列化格式统一为lowerCamelCase。
  
#### 5.MIME类型全部为`Content-Type:application/json;charset=UTF-8`。API响应开启了GZip压缩。
 
#### 6.接口响应的统一包装格式为：
 ```json
{
    "code": 0,
    "msg":null,
    "data": "具体API返回的数据"
}
```

#### 7.Http Header说明
  服务的所有接口需添加accessType的Http Header，其值为web,ios,android,desktop中的一个。
  非PUBLIC权限接口，需添加Authorization的Http Header,其值为Bearer token,注意Bearer和token之间有个空格。
  
