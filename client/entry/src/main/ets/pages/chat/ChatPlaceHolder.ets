@Component
export struct ChatPlaceHolder {
  list: Array<[
    ResourceStr,
    string,
    string,
  ]> = [
    [$r("app.media.sun"), "解释一下什么是幂等，举个例子", "帮我写一个 Spring Boot 接口示例"],
    [$r("app.media.cloud"), "这段 ArkTS 代码为什么编译不过", "帮我润色一封英文邮件"],
    [$r("app.media.flash"), "总结一下这份文档的要点", "推荐几本分布式系统入门书"],
  ];

  build() {
    Column() {
      Text("您好，在这可以")
        .fontSize(24)
        .fontWeight(FontWeight.Bold)
        .fontColor(0xFFFFFF)
        .margin({ top: 10 })

      Text("询问任何感兴趣的话题")
        .fontSize(24)
        .fontWeight(FontWeight.Bold)
        .fontColor(0xFFFFFF)
        .margin({ top: 5 })

      Text("开启您的AI之旅")
        .fontSize(16)
        .fontWeight(FontWeight.Bold)
        .fontColor(0xFF8F9193)
        .margin({ top: 5 })

      ForEach(this.list, (item: [
        ResourceStr,
        string,
        string,
      ]) => {
        Column() {
          Image(item[0]).width(30)
          Row() {
            Row()
              .width(10)
              .height(10)
              .borderRadius(10)
              .backgroundColor(0xFF9C5BD2)

            Text(item[1])
              .fontSize(16)
              .fontWeight(FontWeight.Bold)
              .fontColor(0xFFFFFF)
              .margin({ left: 5 })
          }
          .backgroundColor(0xFF1B1C1E)
          .borderRadius(25)
          .padding({left: 15,right: 15, top: 3, bottom: 3})
          .margin({top: 20})

          Row() {
            Row()
              .width(10)
              .height(10)
              .borderRadius(10)
              .backgroundColor(0xFF9C5BD2)

            Text(item[2])
              .fontSize(16)
              .fontWeight(FontWeight.Bold)
              .fontColor(0xFFFFFF)
              .margin({ left: 5 })
          }
          .backgroundColor(0xFF1B1C1E)
          .borderRadius(25)
          .padding({left: 15,right: 15, top: 3, bottom: 3})
          .margin({top: 20})
        }
        .margin({ top: 40 })
      })
    }
  }
}