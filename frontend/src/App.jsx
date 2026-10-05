import PageTransitionLoader from "./components/layouts/PageTransitionLoader";
import AppRoutes from "./routes/AppRoutes";

function App() {
  return (
    <PageTransitionLoader>
      <AppRoutes />
    </PageTransitionLoader>
  );
}

export default App;
