import NavBar from "./components/layouts/NavBar";
import Footer from "./components/layouts/Footer";
import PageTransitionLoader from "./components/layouts/PageTransitionLoader";
import AppRoutes from "./routes/AppRoutes";

function App() {
  return (
    <PageTransitionLoader>
      <NavBar />
      <AppRoutes />
      <Footer />
    </PageTransitionLoader>
  );
}

export default App;
