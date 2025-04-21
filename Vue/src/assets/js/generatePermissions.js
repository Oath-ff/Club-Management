function generatePermissions(role) {
  const permissions = rolePermissions[role] || { buttons: [], menu: [] };
  const result = {
    code: 200,
    data: permissions,
    msg: "成功"
  };
  return result;
}
export default generatePermissions;
const rolePermissions = {
  管理员: {
    button: {
      useProTable: ["add", "batchAdd", "export", "batchDelete", "status"],
      authButton: ["add", "edit", "delete", "import", "export"]
    },
    menu: [
      {
        path: "/home/index",
        name: "home",
        component: "/home/index",
        meta: {
          icon: "HomeFilled",
          title: "首页",
          isLink: "",
          isHide: false,
          isFull: false,
          isAffix: true,
          isKeepAlive: true
        }
      },
      {
        path: "/Club",
        name: "Club",
        redirect: "/club/list",
        meta: {
          icon: "Management",
          title: "社团管理",
          isLink: "",
          isHide: false,
          isFull: false,
          isAffix: false,
          isKeepAlive: true
        },
        children: [
          {
            path: "/club/createClub",
            name: "createClub",
            component: "/club/createClub/index",
            meta: {
              icon: "DocumentAdd",
              title: "添加社团",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          },
          {
            path: "/club/approve",
            name: "approveClub",
            component: "/club/approve/index",
            meta: {
              icon: "DocumentChecked",
              title: "社团审批",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          },
          {
            path: "/club/list",
            name: "clubList",
            component: "/club/list/index",
            meta: {
              icon: "List",
              title: "社团列表",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          },
          {
            path: "/club/detail",
            name: "clubDetail",
            component: "/club/detail/index",
            props: true,
            meta: {
              icon: "Reading",
              title: "社团详情",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          }
        ]
      },
      {
        path: "/membership",
        name: "menbership",
        redirect: "/membership/list",
        meta: {
          icon: "User",
          title: "成员管理",
          isLink: "",
          isHide: false,
          isFull: false,
          isAffix: false,
          isKeepAlive: true
        },
        children: [
          {
            path: "/membership/add",
            name: "addMembership",
            component: "/membership/add/index",
            meta: {
              icon: "DocumentAdd",
              title: "添加成员",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          },
          {
            path: "/membership/approve",
            name: "approveMembership",
            component: "/membership/approve/index",
            meta: {
              icon: "DocumentChecked",
              title: "审批成员",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          },
          {
            path: "/membership/list",
            name: "membershipList",
            component: "/membership/list/index",
            meta: {
              icon: "List",
              title: "成员列表",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          }
        ]
      },
      {
        path: "/event",
        name: "event",
        redirect: "/event/add",
        meta: {
          icon: "Guide",
          title: "活动管理",
          isLink: "",
          isHide: false,
          isFull: false,
          isAffix: false,
          isKeepAlive: true
        },
        children: [
          {
            path: "/event/add",
            name: "addEvent",
            component: "/event/add/index",
            meta: {
              icon: "DocumentAdd",
              title: "添加活动",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          },
          {
            path: "/event/approve",
            name: "approveEvent",
            component: "/event/approve/index",
            meta: {
              icon: "DocumentChecked",
              title: "活动审批",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          },
          {
            path: "/event/list/:id",
            name: "eventList",
            component: "/event/list/index",
            meta: {
              icon: "List",
              title: "活动列表",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          },
          {
            path: "/event/event/:id",
            name: "eventParticipation",
            component: "/participation/event/index",
            meta: {
              icon: "Document",
              title: "活动参与记录",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          },
          {
            path: "/event/user/:id",
            name: "userParticipation",
            component: "/participation/user/index",
            meta: {
              icon: "User",
              title: "成员参与活动记录",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          }
        ]
      },
      {
        path: "/expense",
        name: "expense",
        redirect: "/expense/list",
        meta: {
          icon: "Coin",
          title: "费用管理",
          isLink: "",
          isHide: false,
          isFull: false,
          isAffix: false,
          isKeepAlive: true
        },
        children: [
          {
            path: "/expense/list",
            name: "expenseList",
            component: "/expense/list/index",
            meta: {
              icon: "List",
              title: "费用申请列表",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          },
          {
            path: "/expense/approve/:id",
            name: "approveExpense",
            component: "/expense/approve/index",
            meta: {
              icon: "DocumentChecked",
              title: "费用审批列表",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          }
        ]
      },
      {
        path: "/notification",
        name: "notification",
        redirect: "/notification/send",
        meta: {
          icon: "Message",
          title: "消息管理",
          isLink: "",
          isHide: false,
          isFull: false,
          isAffix: false,
          isKeepAlive: true
        },
        children: [
          {
            path: "/notification/send",
            name: "sendNotification",
            component: "/notification/send/index",
            meta: {
              icon: "ChatSquare",
              title: "发送消息",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          },
          {
            path: "/notification/user/:id",
            name: "notificationList",
            component: "/notification/user/index",
            meta: {
              icon: "MessageBox",
              title: "消息中心",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          }
        ]
      }
    ]
  },
  团长: {
    button: {
      useProTable: ["add", "batchAdd", "export", "batchDelete", "status"],
      authButton: ["add", "edit", "delete", "import", "export"]
    },
    menu: [
      {
        path: "/home/index",
        name: "home",
        component: "/home/index",
        meta: {
          icon: "HomeFilled",
          title: "首页",
          isLink: "",
          isHide: false,
          isFull: false,
          isAffix: true,
          isKeepAlive: true
        }
      },
      {
        path: "/Club",
        name: "Club",
        redirect: "/club/list",
        meta: {
          icon: "Management",
          title: "我的社团管理",
          isLink: "",
          isHide: false,
          isFull: false,
          isAffix: false,
          isKeepAlive: true
        },
        children: [
          {
            path: "/club/detail",
            name: "clubDetail",
            component: "/club/detail/index",
            props: true,
            meta: {
              icon: "Reading",
              title: "社团详情",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          },
          {
            path: "/club/list",
            name: "clubList",
            component: "/club/list/index",
            meta: {
              icon: "List",
              title: "社团列表",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          },
          {
            path: "/club/approve",
            name: "createdClubs",
            component: "/club/approve/index",
            meta: {
              icon: "DocumentChecked",
              title: "社团创建记录",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          },
          {
            path: "/club/membershipApprove",
            name: "submitClub",
            component: "/club/memberApprove/index",
            meta: {
              icon: "DocumentChecked",
              title: "社团加入记录",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          }
        ]
      },
      {
        path: "/membership",
        name: "menbership",
        redirect: "/membership/list",
        meta: {
          icon: "User",
          title: "成员管理",
          isLink: "",
          isHide: false,
          isFull: false,
          isAffix: false,
          isKeepAlive: true
        },
        children: [
          {
            path: "/membership/add",
            name: "addMembership",
            component: "/membership/add/index",
            meta: {
              icon: "DocumentAdd",
              title: "添加成员",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          },
          {
            path: "/membership/approve",
            name: "approveMembership",
            component: "/membership/approve/index",
            meta: {
              icon: "DocumentChecked",
              title: "审批成员",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          },
          {
            path: "/membership/list",
            name: "membershipList",
            component: "/membership/list/index",
            meta: {
              icon: "List",
              title: "成员列表",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          }
        ]
      },
      {
        path: "/event",
        name: "event",
        redirect: "/event/list",
        meta: {
          icon: "Guide",
          title: "活动管理",
          isLink: "",
          isHide: false,
          isFull: false,
          isAffix: false,
          isKeepAlive: true
        },
        children: [
          {
            path: "/event/add",
            name: "addEvent",
            component: "/event/add/index",
            meta: {
              icon: "DocumentAdd",
              title: "发布活动",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          },
          {
            path: "/event/approve",
            name: "approveEvent",
            component: "/event/approve/index",
            meta: {
              icon: "DocumentChecked",
              title: "活动审批记录",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          },
          {
            path: "/event/list/:id",
            name: "eventList",
            component: "/event/list/index",
            meta: {
              icon: "List",
              title: "活动列表",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          },
          {
            path: "/evet/signIn",
            name: "signInParticipation",
            component: "/participation/signIn/index",
            meta: {
              icon: "Select",
              title: "活动签到",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          },
          {
            path: "/event/event/:id",
            name: "eventParticipation",
            component: "/participation/event/index",
            meta: {
              icon: "Document",
              title: "活动参与记录",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          },
          {
            path: "/event/user/:id",
            name: "userParticipation",
            component: "/participation/user/index",
            meta: {
              icon: "User",
              title: "成员参与活动记录",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          }
        ]
      },
      {
        path: "/expense",
        name: "expense",
        redirect: "/expense/submit",
        meta: {
          icon: "Coin",
          title: "费用管理",
          isLink: "",
          isHide: false,
          isFull: false,
          isAffix: false,
          isKeepAlive: true
        },
        children: [
          {
            path: "/expense/submit",
            name: "submitExpense",
            component: "/expense/submit/index",
            meta: {
              icon: "DocumentAdd",
              title: "费用申请",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          },
          {
            path: "/expense/approve/:id",
            name: "approveExpense",
            component: "/expense/approve/index",
            meta: {
              icon: "DocumentChecked",
              title: "费用审批记录",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          }
        ]
      },
      {
        path: "/notification",
        name: "notification",
        redirect: "/notification/send",
        meta: {
          icon: "Message",
          title: "消息管理",
          isLink: "",
          isHide: false,
          isFull: false,
          isAffix: false,
          isKeepAlive: true
        },
        children: [
          {
            path: "/notification/send",
            name: "sendNotification",
            component: "/notification/send/index",
            meta: {
              icon: "ChatSquare",
              title: "发送消息",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          },
          {
            path: "/notification/user/:id",
            name: "notificationList",
            component: "/notification/user/index",
            meta: {
              icon: "MessageBox",
              title: "消息中心",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          }
        ]
      }
    ]
  },
  用户: {
    button: {
      useProTable: ["add", "batchAdd", "export", "batchDelete", "status"],
      authButton: ["add", "edit", "delete", "import", "export"]
    },
    menu: [
      {
        path: "/home/index",
        name: "home",
        component: "/home/index",
        meta: {
          icon: "HomeFilled",
          title: "首页",
          isLink: "",
          isHide: false,
          isFull: false,
          isAffix: true,
          isKeepAlive: true
        }
      },
      {
        path: "/club",
        name: "club",
        redirect: "/club/list",
        meta: {
          icon: "Management",
          title: "我的社团",
          isLink: "",
          isHide: false,
          isFull: false,
          isAffix: false,
          isKeepAlive: true
        },
        children: [
          {
            path: "/club/createClub",
            name: "createClub",
            component: "/club/createClub/index",
            meta: {
              icon: "DocumentAdd",
              title: "创建社团",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          },
          {
            path: "/club/detail",
            name: "clubDetail",
            component: "/club/detail/index",
            props: true,
            meta: {
              icon: "Reading",
              title: "社团详情",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          },
          {
            path: "/club/list",
            name: "clubList",
            component: "/club/list/index",
            meta: {
              icon: "List",
              title: "社团列表",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          },
          {
            path: "/club/approve",
            name: "createdClubs",
            component: "/club/approve/index",
            meta: {
              icon: "DocumentChecked",
              title: "社团创建记录",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          },
          {
            path: "/club/membershipApprove",
            name: "submitClub",
            component: "/club/memberApprove/index",
            meta: {
              icon: "DocumentChecked",
              title: "社团加入记录",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          }
        ]
      },
      {
        path: "/event",
        name: "event",
        redirect: "/event/list",
        meta: {
          icon: "Guide",
          title: "活动管理",
          isLink: "",
          isHide: false,
          isFull: false,
          isAffix: false,
          isKeepAlive: true
        },
        children: [
          {
            path: "/event/list/:id",
            name: "eventList",
            component: "/event/list/index",
            meta: {
              icon: "List",
              title: "活动列表",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          },
          {
            path: "/event/user/:id",
            name: "userParticipation",
            component: "/participation/user/index",
            meta: {
              icon: "User",
              title: "参与活动记录",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          }
        ]
      },
      {
        path: "/notification",
        name: "notification",
        redirect: "/notification/user",
        meta: {
          icon: "Message",
          title: "我的消息",
          isLink: "",
          isHide: false,
          isFull: false,
          isAffix: false,
          isKeepAlive: true
        },
        children: [
          {
            path: "/notification/user/:id",
            name: "notificationList",
            component: "/notification/user/index",
            meta: {
              icon: "MessageBox",
              title: "消息中心",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          }
        ]
      }
    ]
  },
  游客: {
    button: {
      useProTable: ["add", "batchAdd", "export", "batchDelete", "status"],
      authButton: ["add", "edit", "delete", "import", "export"]
    },
    menu: [
      {
        path: "/home/index",
        name: "home",
        component: "/home/index",
        meta: {
          icon: "HomeFilled",
          title: "首页",
          isLink: "",
          isHide: false,
          isFull: false,
          isAffix: true,
          isKeepAlive: true
        }
      },
      {
        path: "/club",
        name: "club",
        redirect: "/club/list",
        meta: {
          icon: "Management",
          title: "社团浏览",
          isLink: "",
          isHide: false,
          isFull: false,
          isAffix: false,
          isKeepAlive: true
        },
        children: [
          {
            path: "/club/list",
            name: "clubList",
            component: "/club/list/index",
            meta: {
              icon: "List",
              title: "社团列表",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          },
          {
            path: "/club/detail",
            name: "clubDetail",
            component: "/club/detail/index",
            props: true,
            meta: {
              icon: "Reading",
              title: "社团详情",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          }
        ]
      },
      {
        path: "/event",
        name: "event",
        redirect: "/event/list",
        meta: {
          icon: "Guide",
          title: "活动浏览",
          isLink: "",
          isHide: false,
          isFull: false,
          isAffix: false,
          isKeepAlive: true
        },
        children: [
          {
            path: "/event/list/:id",
            name: "eventList",
            component: "/event/list/index",
            meta: {
              icon: "List",
              title: "活动列表",
              isLink: "",
              isHide: false,
              isFull: false,
              isAffix: false,
              isKeepAlive: true
            }
          }
        ]
      }
    ]
  }
};
