"use client";

import { useEffect, useState } from "react";

export function useQueryParam(param: string): string | null {
  const [value, setValue] = useState<string | null>(null);

  useEffect(() => {
    const query = new URLSearchParams(window.location.search);
    setValue(query.get(param));
  }, [param]);

  return value;
}
