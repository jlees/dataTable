package common.modules

import com.tzavellas.sse.guice.ScalaModule
import models.{FileContactService, ContactService}


class ProdModule extends ScalaModule {
  def configure() {
    bind[ContactService].to[FileContactService]
  }
}

class DevModule extends ScalaModule {
  def configure() {
    bind[ContactService].to[FileContactService]
  }
}
