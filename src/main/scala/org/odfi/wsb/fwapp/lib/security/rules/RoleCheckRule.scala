package org.odfi.wsb.fwapp.lib.security.rules

class RoleCheckRule(val role: String) extends AuthenticatedRule {

  this.onDownMessage {
    case req if (!req.upped) =>

      // Message still valid -> go ahead
      securitySessionGetUser(Some(req)) match {
        case Some(user) if (user.hasRole(role)) =>

        // Ok
        case Some(user) =>
          responseToRedirect(req)

        case other =>
          responseToRedirect(req)
      }

    case other =>
  }
}