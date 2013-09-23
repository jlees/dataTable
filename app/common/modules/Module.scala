package common.modules

import com.tzavellas.sse.guice.ScalaModule
import models.{DatabaseContactServiceImpl, FileContactServiceImpl, ContactService}


class ProdModule extends ScalaModule {
  def configure() {
    bind[ContactService].to[FileContactServiceImpl]
  }
}

class DevModule extends ScalaModule {
  def configure() {
    //bind[ContactService].to[DatabaseContactServiceImpl]
    bind[ContactService].to[FileContactServiceImpl]
  }
}
