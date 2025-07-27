import { getFormatedDate } from "react-native-modern-datepicker";

export function calculateStartDate(): string {
  const today = new Date();
  return getFormatedDate(
    new Date(today.setDate(today.getDate())),
    "YYYY/MM/DD"
  );
}
