import { act, renderHook } from "@testing-library/react";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useInactivityLogout } from "./use-inactivity-logout";

const TRENTE_MIN = 30 * 60 * 1000;

describe("useInactivityLogout", () => {
  beforeEach(() => vi.useFakeTimers());
  afterEach(() => vi.useRealTimers());

  it("déconnecte après le délai sans activité", () => {
    const onTimeout = vi.fn();
    renderHook(() => useInactivityLogout(TRENTE_MIN, onTimeout));

    act(() => vi.advanceTimersByTime(TRENTE_MIN - 1));
    expect(onTimeout).not.toHaveBeenCalled();
    act(() => vi.advanceTimersByTime(1));
    expect(onTimeout).toHaveBeenCalledTimes(1);
  });

  it("repousse l'échéance à chaque activité", () => {
    const onTimeout = vi.fn();
    renderHook(() => useInactivityLogout(TRENTE_MIN, onTimeout));

    act(() => vi.advanceTimersByTime(TRENTE_MIN - 5000));
    act(() => {
      window.dispatchEvent(new Event("keydown"));
    });
    act(() => vi.advanceTimersByTime(TRENTE_MIN - 5000));
    expect(onTimeout).not.toHaveBeenCalled();
    act(() => vi.advanceTimersByTime(5000));
    expect(onTimeout).toHaveBeenCalledTimes(1);
  });

  it("nettoie le minuteur au démontage", () => {
    const onTimeout = vi.fn();
    const { unmount } = renderHook(() => useInactivityLogout(TRENTE_MIN, onTimeout));
    unmount();
    act(() => vi.advanceTimersByTime(TRENTE_MIN * 2));
    expect(onTimeout).not.toHaveBeenCalled();
  });
});
