import { useCallback, useContext, useState } from "react";
import { FlatList, StyleSheet, Text, View } from "react-native";
import { AuthContext } from "../store/auth-context";
import { useFocusEffect } from "@react-navigation/native";
import LoadingComponent from "../components/LoadingComponent";
import { API_URL, request } from "../util/requests";

interface User {
  email: string;
  city: string;
  fullName: string;
  country: string;
  zip: string;
}
interface Doctor {
  id: number;
  user: User;
  doctorType: string;
  phoneNumber: string;
  street: string;
}

interface Slot {
  id: number;
  freeDay: string;
  freeTime: string;
}

interface Appointment {
  id: number;
  doctor: Doctor;
  user: User;
  additionalNote: string;
  slot: Slot;
}

export default function DoctorAppointmentsListScreen() {
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const authCtx = useContext(AuthContext);

  useFocusEffect(
    useCallback(() => {
      async function loadFutureAppointmentsOfDoctor() {
        await request<Appointment[]>({
          request: `${API_URL}/api/appointment/future/doctor`,
          token: authCtx.token,
          setIsLoading: setIsLoading,
          onSuccess: (resData: Appointment[]) => setAppointments(resData),
        });
      }
      loadFutureAppointmentsOfDoctor();
    }, [])
  );

  const formatTime = (time: string): string => {
    const [hour, minute] = time.split(":").map(Number);
    const period = hour >= 12 ? "PM" : "AM";
    const formattedHour = hour % 12 || 12;
    return `${formattedHour}:${minute.toString().padStart(2, "0")} ${period}`;
  };

  const formatDate = (date: string): string => {
    const options: Intl.DateTimeFormatOptions = {
      year: "numeric",
      month: "long",
      day: "numeric",
    };
    return new Date(date).toLocaleDateString("en-US", options);
  };

  if (isLoading) {
    return <LoadingComponent />;
  }

  return (
    <View style={styles.container}>
      <FlatList
        style={{
          width: "90%",
          marginTop: 20,
        }}
        data={appointments}
        renderItem={({ item }) => (
          <View
            style={{
              backgroundColor: "#f4f5f2",
              borderRadius: 25,
              height: 200,
              padding: 14,
              justifyContent: "space-evenly",
              shadowColor: "#000",
              shadowOffset: { width: 0, height: 4 },
              shadowOpacity: 0.3,
              shadowRadius: 4,
              elevation: 5,
              marginBottom: 20,
            }}
          >
            <Text style={{ fontSize: 23 }}>
              <Text style={{ fontWeight: "bold" }}>Patient:</Text>{" "}
              {item.user.fullName}
            </Text>
            <View>
              <Text>
                <Text style={{ fontWeight: "bold" }}>Date:</Text>{" "}
                {formatDate(item.slot.freeDay)}
              </Text>
              <Text>
                <Text style={{ fontWeight: "bold" }}>Time:</Text>{" "}
                {formatTime(item.slot.freeTime)}
              </Text>
              <Text>
                <Text style={{ fontWeight: "bold" }}>Country:</Text>{" "}
                {item.doctor.user.country}
              </Text>
              <Text>
                <Text style={{ fontWeight: "bold" }}>Postal Code:</Text>{" "}
                {item.doctor.user.zip}
              </Text>
              <Text>
                <Text style={{ fontWeight: "bold" }}>City:</Text>{" "}
                {item.doctor.user.city}
              </Text>
              <Text>
                <Text style={{ fontWeight: "bold" }}>Street:</Text>{" "}
                {item.doctor.street}
              </Text>
              {item.additionalNote !== "" && (
                <Text>
                  <Text style={{ fontWeight: "bold" }}>Note:</Text>{" "}
                  <Text style={{ fontStyle: "italic" }}>
                    {item.additionalNote}
                  </Text>
                </Text>
              )}
            </View>
          </View>
        )}
        keyExtractor={(item) => item.id.toString()}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "white",
  },
});
