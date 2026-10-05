const ROLE_PERMISSIONS = [
  {
    role: "Guest",
    permissions: "Browse properties, manage own bookings and reviews.",
  },
  {
    role: "Host",
    permissions: "Manage own properties and handle their bookings.",
  },
  {
    role: "Admin",
    permissions:
      "Manage platform amenities and review or administer user accounts.",
  },
];

export default function Permissions() {
  return (
    <main className="container py-12 pb-28 md:py-16 md:pb-16">
      <p className="text-xs font-bold uppercase text-[var(--color-sun-dark)]">
        Administration
      </p>
      <h1 className="mt-3 text-4xl font-bold text-[var(--color-bark-dark)]">
        Permissions
      </h1>
      <p className="mt-3 max-w-2xl text-base leading-7 text-[var(--color-graph)]">
        The available actions are separated by account role and enforced by the
        platform API.
      </p>
      <div className="mt-8 overflow-x-auto rounded border border-[var(--color-bark)]/10 bg-white">
        <table className="w-full min-w-[34rem] text-left text-sm">
          <thead className="bg-[var(--color-bark-dark)] text-white">
            <tr>
              <th className="px-4 py-3 font-semibold">Role</th>
              <th className="px-4 py-3 font-semibold">Capabilities</th>
            </tr>
          </thead>
          <tbody>
            {ROLE_PERMISSIONS.map(({ role, permissions }) => (
              <tr
                key={role}
                className="border-t border-[var(--color-bark)]/10"
              >
                <td className="px-4 py-3 font-bold text-[var(--color-bark-dark)]">
                  {role}
                </td>
                <td className="px-4 py-3 leading-6 text-[var(--color-graph)]">
                  {permissions}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </main>
  );
}
