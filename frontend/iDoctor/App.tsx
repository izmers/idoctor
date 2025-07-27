import { useContext } from "react";
import { StyleSheet } from "react-native";
import AuthContextProvider, { AuthContext } from "./store/auth-context";
import { createBottomTabNavigator } from "@react-navigation/bottom-tabs";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
import { NavigationContainer } from "@react-navigation/native";
import StartScreen from "./screens/StartScreen";
import UserRegistrationScreen from "./screens/UserRegistrationScreen";
import SignInScreen from "./screens/SignInScreen";
import DoctorRegistrationScreen from "./screens/DoctorRegistrationScreen";
import DoctorVerificationScreen from "./screens/DoctorVerificationScreen";
import { Ionicons, MaterialIcons } from "@expo/vector-icons";
import NotAuthenticatedScreen from "./screens/NotAuthenticatedScreen";
import DoctorSearchScreen from "./screens/DoctorSearchScreen";
import Fontisto from "@expo/vector-icons/Fontisto";
import SymptomsInputScreen from "./screens/SymptomsInputScreen";
import AppointmentScreen from "./screens/AppointmentScreen";
import AppointmentsListScreen from "./screens/AppointmentsListScreen";
import FontAwesome5 from "@expo/vector-icons/FontAwesome5";
import SlotsScreenDoctor from "./screens/SlotsScreenDoctor";
import DoctorAppointmentsListScreen from "./screens/DoctorAppointmentsListScreen";
import ChatsScreen from "./screens/ChatsScreen";
import Entypo from "@expo/vector-icons/Entypo";
import PrivateChatScreen from "./screens/PrivateChatScreen";
import ChatRequestsScreen from "./screens/ChatRequestsScreen";
import ResetPasswordEmailVerificationScreen from "./screens/ResetPasswordEmailVerificationScreen";
import ResetPasswordScreen from "./screens/ResetPasswordScreen";
import { LogBox } from "react-native";

LogBox.ignoreAllLogs();

export type RootStackParamList = {
  Main: undefined;
  VerifyEmail: undefined;
  ResetPassword: { email: string };
  UserRegistration: undefined;
  DoctorRegistration: undefined;
  DoctorVerification: { username: string };
  SignIn: undefined;
  Doctors: {
    screen: keyof DoctorsStackParameterList;
    params?: DoctorsStackParameterList[keyof DoctorsStackParameterList];
  };
};

export type DoctorsStackParameterList = {
  DoctorSearch: { recommendedDoctors: Doctor[] };
  FilterCountry: { setFilteredCountry: (country: string) => void };
  Appointment: Doctor;
};

export type AppointmentListStackParameterList = {
  UpcomingAppointments: undefined;
};

export type DoctorSpecificAppointmentListStackParameterList = {
  UpcomingAppointmentsDoctor: undefined;
};

export type DoctorSpecificDoctorStackParameterList = {
  SlotsManagement: undefined;
};

export type ChatStackParameterList = {
  ChatsView: undefined;
  PrivateChat: { username: string; channelId: number };
  ChatRequests: undefined;
};

interface User {
  email: string;
  username: string;
  city: string;
  fullName: string;
}

interface Doctor {
  id: number;
  user: User;
  doctorType: string;
  phoneNumber: string;
  street: string;
}

const linking = {
  prefixes: ["myapp://"], // Prefix for deep links
  config: {
    screens: {
      ResetPassword: {
        path: "reset-password/:email", // Define path and params
        parse: {
          email: (email: string) => email, // Decode the email param
        },
      },
    },
  },
};

const Tab = createBottomTabNavigator();
const Stack = createNativeStackNavigator<RootStackParamList>();
const DoctorsStack = createNativeStackNavigator<DoctorsStackParameterList>();
const AppointmentsListStack =
  createNativeStackNavigator<AppointmentListStackParameterList>();
const DoctorSpecificDoctorStack =
  createNativeStackNavigator<DoctorSpecificDoctorStackParameterList>();
const DoctorSpecificAppointmentsListStack =
  createNativeStackNavigator<DoctorSpecificAppointmentListStackParameterList>();
const ChatStack = createNativeStackNavigator<ChatStackParameterList>();

function ChatStackNavigator() {
  const authCtx = useContext(AuthContext);

  return (
    <ChatStack.Navigator
      screenOptions={{ headerStyle: { backgroundColor: "#f4f5f2" } }}
    >
      <ChatStack.Screen
        name="ChatsView"
        component={ChatsScreen}
        options={{
          headerTitle: "Chats",
          headerTintColor: "black",
        }}
      />

      <ChatStack.Screen name="PrivateChat" component={PrivateChatScreen} />

      <ChatStack.Screen
        name="ChatRequests"
        component={ChatRequestsScreen}
        options={{ headerTitle: "Requests", headerTintColor: "black" }}
      />
    </ChatStack.Navigator>
  );
}

function DoctorSpecificAppointmentsListStackNavigator() {
  const authCtx = useContext(AuthContext);

  return (
    <DoctorSpecificAppointmentsListStack.Navigator
      screenOptions={{ headerStyle: { backgroundColor: "#f4f5f2" } }}
    >
      <DoctorSpecificAppointmentsListStack.Screen
        name="UpcomingAppointmentsDoctor"
        component={DoctorAppointmentsListScreen}
        options={{
          headerTitle: "Upcoming Appointments",
        }}
      />
    </DoctorSpecificAppointmentsListStack.Navigator>
  );
}

function StackNavigator() {
  const authCtx = useContext(AuthContext);

  return (
    <Stack.Navigator
      screenOptions={{ headerStyle: { backgroundColor: "#f4f5f2" } }}
    >
      <Stack.Screen
        name="Main"
        component={authCtx.isAuthenticated ? SymptomsInputScreen : StartScreen}
        options={{ headerShown: false, headerTitle: "Home" }}
      />

      <Stack.Screen
        name="VerifyEmail"
        component={ResetPasswordEmailVerificationScreen}
        options={{
          headerTitle: "Email Verification",
          headerTintColor: "black",
        }}
      />

      <Stack.Screen
        name="ResetPassword"
        component={ResetPasswordScreen}
        options={{
          headerTitle: "Reset Password",
          headerTintColor: "black",
        }}
      />

      <Stack.Screen
        name="UserRegistration"
        component={UserRegistrationScreen}
        options={{
          headerTitle: "",
          headerTintColor: "black",
          headerBackTitle: "Home",
        }}
      />

      <Stack.Screen
        name="DoctorRegistration"
        component={DoctorRegistrationScreen}
        options={{
          headerTitle: "",
          headerTintColor: "black",
          headerBackTitle: "Home",
        }}
      />

      <Stack.Screen
        name="DoctorVerification"
        component={DoctorVerificationScreen}
        options={{
          headerTitle: "",
          headerTintColor: "black",
          headerBackTitle: "Home",
        }}
      />

      <Stack.Screen
        name="SignIn"
        component={SignInScreen}
        options={{
          headerTitle: "",
          headerTintColor: "black",
        }}
      />
    </Stack.Navigator>
  );
}

function DoctorStackNavigator() {
  const authCtx = useContext(AuthContext);

  return (
    <DoctorsStack.Navigator
      screenOptions={{ headerStyle: { backgroundColor: "#f4f5f2" } }}
    >
      <DoctorsStack.Screen
        name="DoctorSearch"
        component={DoctorSearchScreen}
        options={{
          headerTitle: "Doctors",
        }}
      />

      <DoctorsStack.Screen
        name="Appointment"
        component={AppointmentScreen}
        options={{
          headerTitle: "Appointment Booking",
          headerTintColor: "black",
        }}
      />
    </DoctorsStack.Navigator>
  );
}

function DoctorSpecificDoctorStackNavigator() {
  const authCtx = useContext(AuthContext);

  return (
    <DoctorSpecificDoctorStack.Navigator
      screenOptions={{ headerStyle: { backgroundColor: "#f4f5f2" } }}
    >
      <DoctorSpecificDoctorStack.Screen
        name="SlotsManagement"
        component={SlotsScreenDoctor}
        options={{
          headerTitle: "Slots Management",
          headerTintColor: "black",
          headerLeft: () => (
            <MaterialIcons
              name="logout"
              size={24}
              color="black"
              onPress={() => authCtx.logout()}
            />
          ),
        }}
      />
    </DoctorSpecificDoctorStack.Navigator>
  );
}

function AppointmentStackNavigator() {
  const authCtx = useContext(AuthContext);

  return (
    <AppointmentsListStack.Navigator
      screenOptions={{ headerStyle: { backgroundColor: "#f4f5f2" } }}
    >
      <AppointmentsListStack.Screen
        name="UpcomingAppointments"
        component={AppointmentsListScreen}
        options={{
          headerTitle: "Upcoming Appointments",
          headerTintColor: "black",
        }}
      />
    </AppointmentsListStack.Navigator>
  );
}

function MainContainer() {
  const authCtx = useContext(AuthContext);

  return (
    <NavigationContainer linking={linking}>
      <Tab.Navigator
        screenOptions={{
          tabBarActiveTintColor: "black",
          tabBarStyle: authCtx.isAuthenticated
            ? { backgroundColor: "#f4f5f2" }
            : { backgroundColor: "#f4f5f2", display: "none" },
        }}
      >
        {!authCtx.isDoctor && (
          <Tab.Screen
            name="Home"
            component={StackNavigator}
            options={{
              headerShown: false,
              tabBarIcon: ({ color, size }) => (
                <Ionicons name="home" size={size} color={color} />
              ),
            }}
          />
        )}

        {authCtx.isDoctor ? (
          <Tab.Screen
            name="Slots"
            component={
              authCtx.isAuthenticated
                ? DoctorSpecificDoctorStackNavigator
                : NotAuthenticatedScreen
            }
            options={{
              headerShown: false,
              tabBarIcon: ({ color, size }) => (
                <Fontisto name="doctor" size={24} color={color} />
              ),
            }}
          />
        ) : (
          <Tab.Screen
            name="Doctors"
            component={
              authCtx.isAuthenticated
                ? DoctorStackNavigator
                : NotAuthenticatedScreen
            }
            options={{
              headerShown: false,
              tabBarIcon: ({ color, size }) => (
                <Fontisto name="doctor" size={24} color={color} />
              ),
            }}
          />
        )}

        {authCtx.isDoctor ? (
          <Tab.Screen
            name="Appointments"
            component={DoctorSpecificAppointmentsListStackNavigator}
            options={{
              headerShown: false,
              tabBarIcon: ({ color, size }) => (
                <FontAwesome5 name="calendar-day" size={24} color={color} />
              ),
            }}
          />
        ) : (
          <Tab.Screen
            name="Appointments"
            component={
              authCtx.isAuthenticated
                ? AppointmentStackNavigator
                : NotAuthenticatedScreen
            }
            options={{
              headerShown: false,
              tabBarIcon: ({ color, size }) => (
                <FontAwesome5 name="calendar-day" size={24} color={color} />
              ),
            }}
          />
        )}

        <Tab.Screen
          name="Chats"
          component={
            authCtx.isAuthenticated
              ? ChatStackNavigator
              : NotAuthenticatedScreen
          }
          options={{
            headerShown: false,
            tabBarIcon: ({ color, size }) => (
              <Entypo name="chat" size={24} color={color} />
            ),
          }}
        />
      </Tab.Navigator>
    </NavigationContainer>
  );
}

export default function App() {
  return (
    <AuthContextProvider>
      <MainContainer />
    </AuthContextProvider>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#fff",
    alignItems: "center",
    justifyContent: "center",
  },
});
