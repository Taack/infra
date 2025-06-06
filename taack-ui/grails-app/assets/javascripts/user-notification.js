function manageNotificationReading() {
    const title = document.querySelector('#navbarUser[unread-notification-number]')
    if (title !== null) {
        let notificationHeader = document.querySelector('.user-notification-header')
        let notificationBody = document.querySelector('.user-notification-body')
        let notificationGroups = notificationBody.querySelectorAll('.user-notification-group')
        for (let i = 0; i < notificationGroups.length; i++) {
            let group = notificationGroups[i]
            let header = group.querySelector('.group-header')
            let itemSizeSpan = header.querySelector('a[unread-notification-number]')
            let items = group.querySelectorAll('.group-item')
            for (let j = 0; j < items.length; j++) {
                items[j].addEventListener('click', function () {
                    let globalItemSize = parseInt(title.getAttribute("unread-notification-number")) - 1
                    title.setAttribute("unread-notification-number", globalItemSize.toString())
                    if (globalItemSize > 0) {
                        let groupItemSize = parseInt(itemSizeSpan.getAttribute("unread-notification-number")) - 1
                        if (groupItemSize > 0) {
                            itemSizeSpan.setAttribute("unread-notification-number", groupItemSize.toString())
                            items[j].remove()
                        } else {
                            group.remove()
                        }
                    } else {
                        notificationHeader.remove()
                        notificationBody.remove()
                    }
                })
            }
        }
    }
}

manageNotificationReading()
