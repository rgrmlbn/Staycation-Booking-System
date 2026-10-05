import { useUsers } from "../../hooks/useUsers";

function getErrorMessage(error) {
  return (
    error.response?.data?.message ||
    error.message ||
    "Unable to load users. Please try again."
  );
}

export default function Users() {
  const { data: users = [], error, isPending } = useUsers();

  return (
    <main className="container py-12 pb-28 md:py-16 md:pb-16">
      <p className="text-xs font-bold uppercase text-[var(--color-sun-dark)]">
        Administration
      </p>
      <h1 className="mt-3 text-4xl font-bold text-[var(--color-bark-dark)]">
        Users
      </h1>
      <p className="mt-3 text-base leading-7 text-[var(--color-graph)]">
        Review the accounts registered on the platform.
      </p>
      {isPending ? (
        <p className="mt-8" role="status">
          Loading users...
        </p>
      ) : error ? (
        <p className="mt-8 text-sm text-red-700" role="alert">
          {getErrorMessage(error)}
        </p>
      ) : users.length === 0 ? (
        <p className="mt-8 rounded bg-white p-5 text-sm text-[var(--color-graph)]">
          No user accounts were found.
        </p>
      ) : (
        <div className="mt-8 overflow-x-auto rounded border border-[var(--color-bark)]/10 bg-white">
          <table className="w-full min-w-[36rem] text-left text-sm">
            <thead className="bg-[var(--color-bark-dark)] text-white">
              <tr>
                <th className="px-4 py-3 font-semibold">Name</th>
                <th className="px-4 py-3 font-semibold">Email</th>
                <th className="px-4 py-3 font-semibold">Role</th>
              </tr>
            </thead>
            <tbody>
              {users.map((user) => (
                <tr
                  key={user.id}
                  className="border-t border-[var(--color-bark)]/10"
                >
                  <td className="px-4 py-3 font-semibold text-[var(--color-bark-dark)]">
                    {user.name}
                  </td>
                  <td className="px-4 py-3 text-[var(--color-graph)]">
                    {user.email}
                  </td>
                  <td className="px-4 py-3 text-[var(--color-graph)]">
                    {user.role}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </main>
  );
}
