import recordRepo from '../repo/record.repo.js';

function formatDate(date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

function startOfWeek(date) {
  const normalized = new Date(date);
  normalized.setHours(0, 0, 0, 0);
  const day = normalized.getDay();
  const diff = day === 0 ? -6 : 1 - day;
  normalized.setDate(normalized.getDate() + diff);
  return normalized;
}

function endOfWeek(date) {
  const end = new Date(date);
  end.setDate(end.getDate() + 6);
  end.setHours(23, 59, 59, 999);
  return end;
}

const recordService = {
  getWeeklySummary(userId, activityType, weeks = 12) {
    const totalWeeks = Math.min(Math.max(Number(weeks) || 12, 1), 12);
    const currentWeekStart = startOfWeek(new Date());
    const firstWeekStart = new Date(currentWeekStart);
    firstWeekStart.setDate(firstWeekStart.getDate() - (totalWeeks - 1) * 7);

    const rows = recordRepo.getWeeklySummaryRows(
      userId,
      activityType,
      formatDate(firstWeekStart),
      formatDate(endOfWeek(currentWeekStart)),
    );

    const rowMap = new Map(rows.map((row) => [row.week_start, row]));
    const points = [];

    for (let index = 0; index < totalWeeks; index += 1) {
      const weekStart = new Date(firstWeekStart);
      weekStart.setDate(firstWeekStart.getDate() + index * 7);
      const weekEnd = endOfWeek(weekStart);
      const weekKey = formatDate(weekStart);
      const row = rowMap.get(weekKey);

      points.push({
        week_start: weekKey,
        week_end: formatDate(weekEnd),
        total_distance_km: row ? Number(row.total_distance_km) : 0,
        total_duration_seconds: row ? Number(row.total_duration_seconds) : 0,
        total_elevation_gain_m: row ? Number(row.total_elevation_gain_m) : 0,
      });
    }

    return {
      activity_type: activityType,
      weeks: totalWeeks,
      points,
    };
  },
};

export default recordService;
