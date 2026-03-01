<%@ page import="calendar.CalendarDow; calendar.CalendarMonth; java.text.SimpleDateFormat" %>
<%
    // Model: events (List<CalendarEvent>), calendarParams (CalendarParams)
    Calendar cal = Calendar.getInstance()
    cal.set(Calendar.YEAR, calendarParams.year)
    cal.set(Calendar.MONTH, calendarParams.month.ordinal())
    cal.set(Calendar.DAY_OF_MONTH, 1)

    // Day of week for 1st of month (1=Mon .. 7=Sun)
    int firstDow = cal.get(Calendar.DAY_OF_WEEK)  // Java: 1=Sun..7=Sat
    int firstDowMon = firstDow == Calendar.SUNDAY ? 7 : firstDow - 1  // Convert to 1=Mon..7=Sun

    int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

    // Padding days from previous month
    int paddingBefore = firstDowMon - 1

    // Total cells needed (pad to full weeks)
    int totalCells = paddingBefore + daysInMonth
    int paddingAfter = (7 - (totalCells % 7)) % 7
    totalCells += paddingAfter

    System.out.println("AUO 111")

    // Previous month info for padding
    Calendar prevCal = Calendar.getInstance()
    prevCal.set(Calendar.YEAR, calendarParams.year)
    prevCal.set(Calendar.MONTH, calendarParams.month.ordinal())
    prevCal.add(Calendar.MONTH, -1)
    int prevMonthDays = prevCal.getActualMaximum(Calendar.DAY_OF_MONTH)

    // Today
    Calendar today = Calendar.getInstance()
    int todayYear = today.get(Calendar.YEAR)
    int todayMonth = today.get(Calendar.MONTH)
    int todayDay = today.get(Calendar.DAY_OF_MONTH)

    System.out.println("AUO 111222")

    def sdf = new SimpleDateFormat('HH:mm')
%>
<div id="cal-body">
    <div class="cal-month">
        <% CalendarDow.values().each { dn -> %>
            <div class="cal-day-name">${dn}</div>
        <% } %>

        <% for (int i = 0; i < totalCells; i++) {
            int dayNum
            boolean isOtherMonth = false
            boolean isToday = false
            int cellMonth = calendarParams.month.ordinal()
            int cellYear = calendarParams.year

            if (i < paddingBefore) {
                // Previous month
                dayNum = prevMonthDays - paddingBefore + 1 + i
                isOtherMonth = true
                cellMonth = prevCal.get(Calendar.MONTH)
                cellYear = prevCal.get(Calendar.YEAR)
            } else if (i >= paddingBefore + daysInMonth) {

                // Next month
                dayNum = i - paddingBefore - daysInMonth + 1
                isOtherMonth = true
                if (calendarParams.month.ordinal() == 11) { cellMonth = 0; cellYear = calendarParams.year + 1 }
                else { cellMonth = calendarParams.month.ordinal() + 1 }
            } else {

                dayNum = i - paddingBefore + 1
                isToday = (calendarParams.year == todayYear && calendarParams.month.ordinal() == todayMonth && dayNum == todayDay)
            }

            // Find events overlapping this day
            Calendar dayStart = Calendar.getInstance()
            dayStart.set(cellYear, cellMonth, dayNum, 0, 0, 0)
            dayStart.set(Calendar.MILLISECOND, 0)
            Calendar dayEnd = Calendar.getInstance()
            dayEnd.set(cellYear, cellMonth, dayNum, 23, 59, 59)
            dayEnd.set(Calendar.MILLISECOND, 999)

            def dayEvents = events.findAll { ev ->
                ev.fromDate.before(dayEnd.time) && ev.toDate.after(dayStart.time)
            }

            String cellClass = 'cal-day-cell'
            if (isToday) cellClass += ' cal-today'
            if (isOtherMonth) cellClass += ' cal-other-month'
        %>

        <div class="${cellClass}">
                <div class="cal-day-number">${dayNum}</div>
                <% dayEvents.eachWithIndex { ev, idx ->
                    String color = '#4EA4DD'
                %>
                    <span class="cal-event-chip" style="background: ${color};"
                          title="${sdf.format(ev.fromDate)} - ${sdf.format(ev.toDate)}: ${ev.title}">
                        ${ev.title}
                    </span>
                <% } %>
            </div>
        <% } %>
    </div>
</div>
